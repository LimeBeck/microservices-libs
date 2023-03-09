package dev.limebeck.libs.database.jooq

import dev.limebeck.libs.model.PaginatedResult
import dev.limebeck.libs.models.Pagination
import dev.limebeck.libs.models.ifNotNull
import org.jooq.*
import org.jooq.impl.DSL
import java.sql.Timestamp
import java.time.Instant


fun <AfterWhereStep : Select<Record>, T> DSLContext.selectPaginated(
    mapper: RecordMapper<Record, T>,
    pagination: Pagination? = null,
    selection: SelectSelectStep<Record> = select(),
    orderByStep: (AfterWhereStep.() -> SelectLimitStep<Record>)? = null,
    whereStep: (SelectSelectStep<Record>.() -> AfterWhereStep)? = null
): PaginatedResult<T> {
    @Suppress("unchecked_cast")
    val total = (selectCount() as SelectSelectStep<Record>)
        .ifNotNull(whereStep) { it(this) }
        .fetchOne()!!.get(0, Int::class.java)!!

    val filteredAndOrderedSelection = if (whereStep != null) {
        val filteredSelection = whereStep(selection)
        if (orderByStep != null) {
            orderByStep(filteredSelection)
        } else {
            filteredSelection
        }
    } else {
        selection
    }

    val list = (filteredAndOrderedSelection as SelectLimitStep<Record>)
        .ifNotNull(pagination) {
            offset(it.page * it.limit)
            limit(it.limit)
        }
        .fetch(mapper)

    return PaginatedResult(
        total = total,
        page = pagination?.page ?: 0,
        limit = pagination?.limit ?: list.size,
        list = list
    )
}

@Suppress("unchecked_cast")
fun <AfterWhereStep : Select<Record>, T> DSLContext.selectPaginatedWithGroup(
    mapper: RecordMapper<Record, T>,
    pagination: Pagination? = null,
    countOn: List<TableDefinitionField<*>>,
    groupStep: (AfterWhereStep.() -> SelectHavingStep<Record>)? = null,
    selectionStep: DSLContext.() -> SelectSelectStep<Record> = { select() },
    orderByStep: (AfterWhereStep.() -> SelectLimitStep<Record>)? = null,
    whereStep: (SelectSelectStep<Record>.() -> AfterWhereStep)? = null
): PaginatedResult<T> {
    @Suppress("unchecked_cast")
    val total = (select(DSL.countDistinct(*(countOn.map { it.asField }.toTypedArray()))) as SelectSelectStep<Record>)
        .ifNotNull(whereStep) { it(this) }
        .fetchOne()!!.get(0, Int::class.java)!!

    val selection = selectionStep()

    val filteredSelection = if (whereStep != null) {
        whereStep(selection)
    } else {
        selection
    }

    val orderedSelection = if (orderByStep != null) {
        orderByStep(filteredSelection as AfterWhereStep)
    } else {
        filteredSelection
    }

    val list = (orderedSelection as SelectLimitStep<Record>)
        .ifNotNull(groupStep) {
            it(this as AfterWhereStep)
        }
        .ifNotNull(pagination) {
            offset(it.page * it.limit)
            limit(it.limit)
        }
        .fetch(mapper)

    return PaginatedResult(
        total = total,
        page = pagination?.page ?: 0,
        limit = pagination?.limit ?: list.size,
        list = list
    )
}

/**
 * Set operator for insert and update query.
 *
 * @sample
 * dsl.insertInto(asTable)
 *   .insertValues(
 *     ID set UUID.fromString(client.id!!),
 *     USER_ID set client.userId,
 *     EXTERNAL_ID set client.externalId
 *   ).executeAndCheck()
 */
infix fun <T> TableDefinitionField<T>.set(value: T): Pair<Field<T>, T> = asField to value

/**
 *  Inserts values
 */
fun <R : Record> InsertSetStep<R>.insertValues(vararg columnValues: Pair<Field<*>, *>): InsertOnDuplicateStep<R> =
    columns(columnValues.map { it.first }).values(columnValues.map { it.second })

/**
 *  Inserts values
 */
@JvmName("insertDefValues")
fun <R : Record> InsertSetStep<R>.insertValues(vararg columnValues: Pair<TableDefinitionField<*>, *>): InsertOnDuplicateStep<R> =
    columns(columnValues.map { it.first.asField }).values(columnValues.map { it.second })

fun <R : Record> UpdateSetFirstStep<R>.set(vararg columnValues: Pair<Field<*>, *>): UpdateSetMoreStep<R> =
    set(columnValues.toMap())

@JvmName("setDefs")
fun <R : Record> UpdateSetFirstStep<R>.set(vararg columnValues: Pair<TableDefinitionField<*>, *>): UpdateSetMoreStep<R> =
    set(columnValues.toMap())

infix fun <T> TableDefinitionField<T>.eq(value: T) =
    asField.eq(value)

infix fun <T> TableDefinitionField<T>.eq(field: TableDefinitionField<T>) =
    asField.eq(field.asField)

infix fun <T> Field<T>.eq(field: Field<T>): Condition = eq(field)
infix fun <T> Field<T>.ne(field: Field<T>): Condition = ne(field)
infix fun <T> Field<T>.le(value: T): Condition = le(value)
infix fun <T> Field<T>.le(field: Field<T>): Condition = le(field)
infix fun <T> Field<T>.lt(field: Field<T>): Condition = lt(field)
infix fun <T> Field<T>.ge(value: T): Condition = ge(value)
infix fun <T> Field<T>.ge(field: Field<T>): Condition = ge(field)
infix fun <T> Field<T>.gt(field: Field<T>): Condition = gt(field)

infix fun <T> TableDefinitionField<T>.ne(value: T) = asField.ne(value)

val <T> TableDefinitionField<T>.isNull: Condition
    get() = asField.isNull

val <T> TableDefinitionField<T>.isNotNull: Condition
    get() = asField.isNotNull

infix fun Condition.and(condition: Condition) =
    and(condition)

infix fun Condition.or(condition: Condition) =
    or(condition)

operator fun <T> Record.get(field: TableDefinitionField<T>): T = this[field.asField]

/**
 * Checks that execution affects expected number of rows.
 */
fun Query.executeAndCheck(affectedRows: Int = 1) {
    if (execute() != affectedRows)
        throw RuntimeException("<48375cef> Execution of query affects unexpected number of rows")
}

fun Instant.toTimestamp(): Timestamp = Timestamp.from(this)