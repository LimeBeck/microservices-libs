package dev.limebeck.libs.db

import kotlinx.atomicfu.AtomicInt

interface TransactionWithDBConnection<T : Any> : Transaction<T> {
    val connection: T

    val childTransactionsCounter: AtomicInt

    override val hasChildTransactions: Boolean
}