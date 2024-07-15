package dev.limebeck.libs.db.jooq

import dev.limebeck.libs.db.DatabaseService
import dev.limebeck.libs.db.runWithTransaction
import org.jooq.DSLContext

interface JooqDatabaseService : DatabaseService<DSLContext>, AutoCloseable

inline fun <R> JooqDatabaseService.runTransactionAndGetContext(crossinline block: (ctx: DSLContext) -> R): R =
    runWithTransaction { transaction ->
        withDslContext(transaction) { ctx ->
            block(ctx)
        }
    }