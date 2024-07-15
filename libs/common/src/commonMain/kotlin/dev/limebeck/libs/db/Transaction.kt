package dev.limebeck.libs.db

import dev.limebeck.libs.scopes.Scope

interface Transaction<T> : AutoCloseable {

    val parentTransaction: Transaction<T>?
    val hasChildTransactions: Boolean

    fun commit(scope: Scope)
    fun rollback(scope: Scope)
}
