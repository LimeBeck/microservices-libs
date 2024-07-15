package dev.limebeck.libs.db.jooq

import dev.limebeck.libs.db.*
import dev.limebeck.libs.errors.InvalidOperationException
import dev.limebeck.libs.logger.logger
import dev.limebeck.libs.scopes.Scope
import io.micrometer.core.instrument.MeterRegistry
import kotlinx.atomicfu.AtomicInt
import kotlinx.atomicfu.atomic
import org.jooq.DSLContext
import org.jooq.impl.DSL
import org.jooq.tools.jdbc.JDBCUtils
import java.io.Closeable
import java.sql.Connection
import java.sql.Savepoint
import java.util.concurrent.atomic.AtomicInteger

class JooqDatabaseServiceImpl(
    configuration: DbConfiguration,
    dataSourceProvider: DataSourceProvider = PostgresDataSourceProvider,
    val alwaysRollback: Boolean = false,
    meterRegistry: MeterRegistry? = null //TODO: Replace with MetricsService
) : JooqDatabaseService {
    companion object {
        val logger = JooqDatabaseServiceImpl::class.logger()
    }

    enum class Metrics(val value: String) {
        TRANSACTIONS_ROOT_COUNTER("transactions.root.counter"),
        TRANSACTIONS_ROOT_OPENED("transactions.root.opened"),
        TRANSACTIONS_INNER_COUNTER("transactions.inner.counter"),
        TRANSACTIONS_INNER_OPENED("transactions.inner.opened")
    }

    private val dataSource = dataSourceProvider.getDataSource(configuration)

    private val rootTransactionsCounter = meterRegistry?.counter(Metrics.TRANSACTIONS_ROOT_COUNTER.value)
    private val openedRootTransactionsGauge =
        meterRegistry?.gauge(Metrics.TRANSACTIONS_ROOT_OPENED.value, AtomicInteger(0))
    private val innerTransactionsCounter = meterRegistry?.counter(Metrics.TRANSACTIONS_INNER_COUNTER.value)
    private val openedInnerTransactionsGauge =
        meterRegistry?.gauge(Metrics.TRANSACTIONS_INNER_OPENED.value, AtomicInteger(0))

    private inner class DBTransaction(
        val scope: Scope,
        override val connection: Connection
    ) : TransactionWithDBConnection<Connection> {

        override val childTransactionsCounter: AtomicInt = atomic(0)

        override val hasChildTransactions: Boolean
            get() = childTransactionsCounter.value > 0

        override val parentTransaction: Transaction<Connection>?
            get() = null

        override fun commit(scope: Scope) {
            if (alwaysRollback) {
                logger.warn { "<46749ee7> $scope Rollback transaction due to settings 'alwaysRollback'" }
                connection.rollback()
            } else {
                logger.debug { "<d3aabbfb> $scope Commit transaction" }
                connection.commit()
            }
        }

        override fun rollback(scope: Scope) {
            logger.debug { "<a7bc1580> $scope Rollback transaction" }
            connection.rollback()
        }

        override fun close() {
            logger.debug { "<526ddc7e> $scope Close transaction" }
            if (hasChildTransactions)
                throw InvalidOperationException("<9e69f792> There is an opened child transactions")
            if (connection.isClosed)
                throw InvalidOperationException("<0fd7b3da> Connection already closed")
            connection.close()
            openedRootTransactionsGauge?.decrementAndGet()
        }
    }

    private inner class DBNestedTransaction(
        val scope: Scope,
        override val parentTransaction: TransactionWithDBConnection<Connection>,
        override val connection: Connection,
        val savepoint: Savepoint
    ) : TransactionWithDBConnection<Connection> {

        init {
            parentTransaction.childTransactionsCounter.incrementAndGet()
        }

        override val childTransactionsCounter = atomic(0)

        override val hasChildTransactions: Boolean
            get() = childTransactionsCounter.value > 0

        override fun commit(scope: Scope) {
            logger.debug { "<0b2ac98c> $scope Commit transaction" }
        }

        override fun rollback(scope: Scope) {
            logger.debug { "<a09126d6> $scope Rollback transaction" }
            connection.rollback(savepoint)
        }

        override fun close() {
            logger.debug { "<fa8ba993> $scope Close transaction" }
            if (hasChildTransactions)
                throw InvalidOperationException("<ebc3d875> There is an opened child transactions")
            connection.releaseSavepoint(savepoint)
            parentTransaction.childTransactionsCounter.decrementAndGet()
            openedInnerTransactionsGauge?.decrementAndGet()
        }
    }

    override fun close() {
        (dataSource as? Closeable)?.close()
    }

    override fun <R> withDslContext(transaction: Transaction<Connection>, block: (DSLContext) -> R): R {
        val connection = (transaction as TransactionWithDBConnection).connection
        val dialect = JDBCUtils.dialect(connection)
        return block(DSL.using(connection, dialect))
    }

    override fun createTransaction(scope: Scope, parentTransaction: Transaction<Connection>?): Transaction<Connection> {
        logger.debug { "<4fb7bf3c> $scope Start ${if (parentTransaction != null) "nested transaction" else "transaction"}" }

        return if (parentTransaction != null) {
            val connection = (parentTransaction as TransactionWithDBConnection).connection
            innerTransactionsCounter?.increment()
            openedInnerTransactionsGauge?.incrementAndGet()
            DBNestedTransaction(scope, parentTransaction, connection, connection.setSavepoint())
        } else {
            val connection = dataSource.connection
            connection.autoCommit = false
            rootTransactionsCounter?.increment()
            openedRootTransactionsGauge?.incrementAndGet()
            DBTransaction(scope, connection)
        }
    }
}