package dev.limebeck.libs.database

import dev.limebeck.libs.scopes.Scope

interface Transaction<T> : AutoCloseable {

    val parentTransaction: Transaction<T>?
    val hasChildTransactions: Boolean

    fun commit(scope: Scope)
    fun rollback(scope: Scope)
}
