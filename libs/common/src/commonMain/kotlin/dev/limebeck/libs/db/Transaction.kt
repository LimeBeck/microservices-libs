package dev.limebeck.libs.db

import dev.limebeck.libs.scopes.Scope

interface Transaction : AutoCloseable {

    val parentTransaction: Transaction?
    val hasChildTransactions: Boolean

    fun commit(scope: Scope)
    fun rollback(scope: Scope)
}
