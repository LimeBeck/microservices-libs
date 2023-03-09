package dev.limebeck.libs.context

import dev.limebeck.libs.database.Transaction
import dev.limebeck.libs.scopes.ScopeHolder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.runBlocking

interface ApplicationContext<TransactionType> : ScopeHolder, AutoCloseable {
    val coroutineScope: CoroutineScope
    fun <T> withTransaction(block: suspend (transaction: Transaction<TransactionType>) -> T): T =
        runBlocking {
            withTransactionSuspend(block)
        }

    suspend fun <T> withTransactionSuspend(block: suspend (transaction: Transaction<TransactionType>) -> T): T
}