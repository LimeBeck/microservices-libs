package dev.limebeck.libs.db

import dev.limebeck.libs.scopes.Scope

interface TransactionService {
    fun createTransaction(scope: Scope, parentTransaction: Transaction? = null): Transaction
}

inline fun <T> TransactionService.runWithTransaction(
    scope: Scope = Scope.EMPTY,
    parentTransaction: Transaction? = null,
    block: (transaction: Transaction) -> T
): T {
    val transaction = createTransaction(scope, parentTransaction)
    return try{
        block(transaction).also {
            transaction.commit(scope)
        }
    } catch (e: Throwable) {
        transaction.rollback(scope)
        throw e
    } finally {
        transaction.close()
    }
}
