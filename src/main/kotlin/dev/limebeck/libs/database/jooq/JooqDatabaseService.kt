package dev.limebeck.libs.database.jooq

import dev.limebeck.libs.database.DatabaseService
import dev.limebeck.libs.database.runWithTransaction
import org.jooq.DSLContext

interface JooqDatabaseService : DatabaseService<DSLContext>, AutoCloseable

inline fun <R> JooqDatabaseService.runTransactionAndGetContext(crossinline block: (ctx: DSLContext) -> R): R =
    runWithTransaction { transaction ->
        withDslContext(transaction) { ctx ->
            block(ctx)
        }
    }