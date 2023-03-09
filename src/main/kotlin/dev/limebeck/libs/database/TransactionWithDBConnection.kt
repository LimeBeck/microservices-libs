package dev.limebeck.libs.database

import java.util.concurrent.atomic.AtomicInteger

interface TransactionWithDBConnection <T: Any> : Transaction<T> {
    val connection: T

    val childTransactionsCounter: AtomicInteger

    override val hasChildTransactions: Boolean
        get() = childTransactionsCounter.get() > 0
}