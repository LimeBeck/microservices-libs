package dev.limebeck.libs.database

import java.sql.Connection

interface DatabaseService<Context> : TransactionService<Connection> {
    fun <R> withDslContext(transaction: Transaction<Connection>, block: (Context) -> R): R
}

