package db

import dev.limebeck.libs.database.FlywayMigrationService
import dev.limebeck.libs.database.jooq.JooqDatabaseService
import dev.limebeck.libs.database.jooq.JooqDatabaseServiceImpl
import dev.limebeck.libs.database.jooq.TableDefinition
import dev.limebeck.libs.database.jooq.runTransactionAndGetContext
import dev.limebeck.libs.database.runWithTransaction
import dev.limebeck.libs.logger.logger
import io.micrometer.core.instrument.MeterRegistry
import io.micrometer.core.instrument.simple.SimpleMeterRegistry
import kotlinx.coroutines.*
import org.jooq.impl.DSL
import org.testcontainers.containers.PostgreSQLContainer
import java.util.concurrent.atomic.AtomicInteger
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFails
import kotlin.time.DurationUnit
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime
import kotlin.time.toDuration

object TestTable : TableDefinition("test") {
    val ID by field<Long>()
    val TEXT by field<String>("text")
}

internal class KPostgreSQLContainer(image: String) : PostgreSQLContainer<KPostgreSQLContainer>(image)

class DatabaseServiceTest {
    private val postgres = KPostgreSQLContainer("postgres:15")
        .withCommand("postgres -c max_connections=100")
        .withUsername("sa")
        .withPassword("sa")
        .apply {
            start()
        }

    private fun createInMemoryDataService(meterRegistry: MeterRegistry? = null): JooqDatabaseService {
        val service = JooqDatabaseServiceImpl(
            dbUrl = "jdbc:h2:mem:test",
            dbUsername = "sa",
            dbPassword = "sa",
            dbDriver = "org.h2.Driver",
            meterRegistry = meterRegistry
        )
        val migrationService = FlywayMigrationService(
            dbUrl = "jdbc:h2:mem:test",
            dbUsername = "sa",
            dbPassword = "sa",
            dbDriver = "org.h2.Driver",
            clean = true
        )
        migrationService.migrate()
        return service
    }

    private fun createPostgresDataService(meterRegistry: MeterRegistry? = null): JooqDatabaseService {
        val service = JooqDatabaseServiceImpl(
            dbUrl = postgres.jdbcUrl,
            dbUsername = postgres.username,
            dbPassword = postgres.password,
            dbDriver = "org.postgresql.Driver",
            meterRegistry = meterRegistry,
            dbMaxPoolSize = 0,
            alwaysRollback = false
        )
        val migrationService = FlywayMigrationService(
            dbUrl = postgres.jdbcUrl,
            dbUsername = postgres.username,
            dbPassword = postgres.password,
            dbDriver = "org.postgresql.Driver",
            clean = true
        )
        migrationService.migrate()
        return service
    }

    private fun JooqDatabaseService.createSchema() {
        runTransactionAndGetContext { ctx ->
            with(TestTable) {
                assertEquals(
                    0,
                    ctx.createTable(asTable).columns(ID.asField, TEXT.asField).execute()
                )
            }
        }
    }

    @ExperimentalTime
    @Test
    fun `Load test with postgres`() {
        val logger = DatabaseServiceTest::class.logger
        val databaseService = createPostgresDataService()
        databaseService.createSchema()

        val successCounter = AtomicInteger(0)
        val errorsCounter = AtomicInteger(0)

        databaseService.runTransactionAndGetContext { ctx ->
            ctx.selectCount().from("pg_stat_activity")
                .where(DSL.field("datname").eq(postgres.databaseName))
                .fetch { r ->
                    logger.debug("<a67b94f5> pg_stat_activity in idle: \n$r")
                }
        }

        val parallelCount = 50
        val sleepTimeoutSec = 2
        val duration = measureTime {
            val jobs = (1..parallelCount).map {
                val job = Job()
                val scope = CoroutineScope(Dispatchers.IO + job)
                scope.launch {
                    try {
                        with(TestTable) {
                            databaseService.runTransactionAndGetContext { ctx ->
                                assertEquals(
                                    1,
                                    ctx.insertInto(asTable)
                                        .columns(ID.asField, TEXT.asField)
                                        .values(10L, "test")
                                        .execute()
                                )
                            }

                            databaseService.runTransactionAndGetContext { ctx ->
                                assertEquals(
                                    listOf("test"),
                                    ctx.select().from(asTable).fetch { r ->
                                        r[TEXT.asField]
                                    }.distinct()
                                )
                            }
                        }

                        databaseService.runTransactionAndGetContext { ctx ->
                            ctx.select(
                                DSL.count(), DSL.function("pg_sleep", Any::class.java, DSL.inline(sleepTimeoutSec))
                            )
                                .from("pg_stat_activity")
                                .where(DSL.field("datname").eq(postgres.databaseName))
                                .fetch {
                                    logger.debug("<150cb32e> pg_stat_activity: \n$it")
                                }
                        }
                        successCounter.incrementAndGet()
                    } catch (e: Throwable) {
                        logger.error("<4162b050> Error", e)
                        errorsCounter.incrementAndGet()
                    }
                }
            }

            runBlocking {
                jobs.joinAll()
            }
        }

        databaseService.runTransactionAndGetContext { ctx ->
            ctx.selectCount().from("pg_stat_activity")
                .where(DSL.field("datname").eq(postgres.databaseName))
                .fetch { r ->
                    logger.debug("<a67b94f5> pg_stat_activity in idle: \n$r")
                }
        }

        assertEquals(0, errorsCounter.get())
        assertEquals(parallelCount, successCounter.get())
        logger.debug("<f243e261> Duration: $duration")
        assert(
            duration.minus(sleepTimeoutSec.toDuration(DurationUnit.SECONDS)) < 1.toDuration(DurationUnit.SECONDS)
        ) { "<50e35ef2> To much long execution" }
    }

    @Test
    fun `Test base transaction`() {
        val databaseService = createInMemoryDataService()
        databaseService.createSchema()

        with(TestTable) {

            databaseService.runTransactionAndGetContext { ctx ->
                assertEquals(
                    1,
                    ctx.insertInto(asTable)
                        .columns(ID.asField, TEXT.asField)
                        .values(10L, "test")
                        .execute()
                )
            }

            assertFails {
                databaseService.runTransactionAndGetContext { ctx ->
                    assertEquals(
                        1,
                        ctx.insertInto(asTable)
                            .columns(ID.asField, TEXT.asField)
                            .values(20L, "test2")
                            .execute()
                    )
                    throw RuntimeException()
                }
            }

            databaseService.runTransactionAndGetContext { ctx ->
                assertEquals(listOf("test"),
                    ctx.select().from(asTable).fetch { r ->
                        r[TEXT.asField]
                    }
                )
            }
        }
    }

    @Test
    fun `Test async transaction`() {
        val databaseService = createInMemoryDataService()
        databaseService.createSchema()
        with(TestTable) {
            runBlocking {
                databaseService.runWithTransaction { transaction ->

                    databaseService.withDslContext(transaction) { dsl ->
                        assertEquals(
                            1,
                            dsl.insertInto(asTable).columns(ID.asField).values(1L).execute()
                        )
                    }

                    delay(100)

                    databaseService.withDslContext(transaction) { dsl ->
                        assertEquals(
                            1,
                            dsl.insertInto(asTable).columns(ID.asField).values(2L).execute()
                        )
                    }

                }
            }

            databaseService.runWithTransaction { transaction ->
                databaseService.withDslContext(transaction) { dsl ->
                    assertEquals(listOf(1L, 2L), dsl.select().from(asTable).fetch { it[ID.asField] })
                }
            }
        }
    }

    @Test
    fun `Test inner transactions`() {
        val databaseService = createInMemoryDataService()
        databaseService.createSchema()

        with(TestTable) {

            databaseService.runWithTransaction { rootTransaction ->
                databaseService.withDslContext(rootTransaction) { dsl ->
                    assertEquals(
                        1,
                        dsl.insertInto(asTable).columns(ID.asField).values(1L).execute()
                    )
                    assertEquals(1, dsl.selectCount().from(asTable).fetch()[0][0])
                }

                assertFails {
                    databaseService.runWithTransaction(parentTransaction = rootTransaction) { innerTransaction ->
                        databaseService.withDslContext(innerTransaction) { dsl ->
                            assertEquals(
                                1,
                                dsl.insertInto(asTable).columns(ID.asField).values(2L).execute()
                            )
                            assertEquals(2, dsl.selectCount().from(asTable).fetch()[0][0])
                        }
                        throw RuntimeException()
                    }
                }

                databaseService.withDslContext(rootTransaction) { dsl ->
                    assertEquals(1, dsl.selectCount().from(asTable).fetch()[0][0])
                }

                databaseService.runWithTransaction(parentTransaction = rootTransaction) { innerTransaction ->
                    databaseService.withDslContext(innerTransaction) { dsl ->
                        assertEquals(
                            1,
                            dsl.insertInto(asTable).columns(ID.asField).values(3L).execute()
                        )
                    }
                }
            }

            databaseService.runWithTransaction { rootTransaction ->
                databaseService.withDslContext(rootTransaction) { dsl ->
                    assertEquals(listOf(1L, 3L), dsl.select().from(asTable).fetch { it[ID.asField] })
                }
            }
        }
    }

    @Test
    fun `Test micrometer`() {
        val registry = SimpleMeterRegistry()
        val databaseService = createInMemoryDataService(registry)

        databaseService.runWithTransaction { }

        databaseService.runWithTransaction { transaction ->
            databaseService.runWithTransaction(parentTransaction = transaction) {
                assertEquals(
                    1.0,
                    registry.find(JooqDatabaseServiceImpl.Metrics.TRANSACTIONS_ROOT_OPENED.value).gauge()?.value()
                )
                assertEquals(
                    1.0,
                    registry.find(JooqDatabaseServiceImpl.Metrics.TRANSACTIONS_INNER_OPENED.value).gauge()?.value()
                )
            }
        }

        assertEquals(
            2.0,
            registry.find(JooqDatabaseServiceImpl.Metrics.TRANSACTIONS_ROOT_COUNTER.value).counter()?.count()
        )
        assertEquals(
            0.0,
            registry.find(JooqDatabaseServiceImpl.Metrics.TRANSACTIONS_ROOT_OPENED.value).gauge()?.value()
        )
        assertEquals(
            1.0,
            registry.find(JooqDatabaseServiceImpl.Metrics.TRANSACTIONS_INNER_COUNTER.value).counter()?.count()
        )
        assertEquals(
            0.0,
            registry.find(JooqDatabaseServiceImpl.Metrics.TRANSACTIONS_INNER_OPENED.value).gauge()?.value()
        )
    }
}

