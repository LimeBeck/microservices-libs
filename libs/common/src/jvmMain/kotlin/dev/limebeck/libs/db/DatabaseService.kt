package dev.limebeck.libs.db

import java.sql.Connection

interface DatabaseService<Context> : TransactionService {
    fun <R> withDslContext(transaction: Transaction, block: (Context) -> R): R
}

