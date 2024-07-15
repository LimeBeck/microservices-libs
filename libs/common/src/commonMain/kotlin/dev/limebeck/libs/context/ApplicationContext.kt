package dev.limebeck.libs.context

import dev.limebeck.libs.db.Transaction
import dev.limebeck.libs.scopes.ScopeHolder
import kotlinx.coroutines.CoroutineScope

/**
 *
 */
interface ApplicationContext<TransactionType> : ScopeHolder, AutoCloseable {
    val coroutineScope: CoroutineScope
    fun <T> withTransaction(block: suspend (transaction: Transaction<TransactionType>) -> T): T
    suspend fun <T> withTransactionSuspend(block: suspend (transaction: Transaction<TransactionType>) -> T): T
}
