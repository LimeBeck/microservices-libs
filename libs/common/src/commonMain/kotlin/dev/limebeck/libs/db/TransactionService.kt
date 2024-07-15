package dev.limebeck.libs.db

import dev.limebeck.libs.scopes.Scope

interface TransactionService<T> {
    fun createTransaction(scope: Scope, parentTransaction: Transaction<T>? = null): Transaction<T>
}

inline fun <T, R : Any> TransactionService<R>.runWithTransaction(
    scope: Scope = Scope.EMPTY,
    parentTransaction: Transaction<R>? = null,
    block: (transaction: Transaction<R>) -> T
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
