package dev.limebeck.libs.database.jooq

import org.jooq.Field
import org.jooq.Name
import org.jooq.Record
import org.jooq.impl.DSL
import java.sql.Timestamp
import kotlin.reflect.KProperty
import kotlin.reflect.full.isSubtypeOf
import kotlin.reflect.full.memberProperties
import kotlin.reflect.full.starProjectedType

abstract class TableDefinition(
    val tableName: String
) {
    val asTable: org.jooq.Table<Record> = DSL.table(tableName)

    inline fun <reified T> field(name: String? = null): TableDefinitionFieldDelegate<T> =
        TableDefinitionFieldDelegate(name, T::class.java)

    fun aliasTo(originalTable: TableDefinition) = originalTable.asTable.`as`(tableName)

    private val allFieldDefinitions: List<TableDefinitionField<*>> by lazy {
        javaClass.kotlin.memberProperties.map { m ->
            if (!m.returnType.isSubtypeOf(TableDefinitionField::class.starProjectedType))
                return@map null
            val res = m.get(this)
            if (res is TableDefinitionField<*>)
                res
            else
                null
        }.filterNotNull()
    }

    val aliasedAllFields: List<Field<*>> by lazy {
        allFieldDefinitions.map { it.asAlias }
    }

    val allFields: List<Field<*>> by lazy {
        allFieldDefinitions.map { it.asField }
    }

}

class TableDefinitionField<T>(
    tableDefinition: TableDefinition,
    val name: String,
    valueClass: Class<T>
) {
    val full: Name = DSL.name(tableDefinition.tableName, name)

    val asField: Field<T> = DSL.field(full, valueClass)

    val aliasName: String = "__${tableDefinition.tableName}__$name"

    val asAlias: Field<T> = asField.`as`(aliasName)
}

class TableDefinitionFieldDelegate<T>(
    val name: String?,
    val clazz: Class<T>
) {
    operator fun getValue(thisRef: TableDefinition, property: KProperty<*>): TableDefinitionField<T> {
        return TableDefinitionField(thisRef, name ?: property.name.lowercase(), clazz)
    }
}