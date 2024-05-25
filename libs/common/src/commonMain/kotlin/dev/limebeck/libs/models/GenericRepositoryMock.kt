package dev.limebeck.libs.models

import dev.limebeck.libs.errors.ObjectNotFoundException
import kotlin.reflect.KProperty

class GenericRepositoryMock<ID : Any, T : Any>(
    val name: String,
    initialRepo: Map<ID, T> = mapOf()
) {
    val repo = mutableMapOf<ID, T>()

    val values = repo.values

    init {
        repo.putAll(initialRepo)
    }

    fun getById(id: ID): T {
        return repo[id]
            ?: throw ObjectNotFoundException("<42e68bb4> Repo $name: object not found by $id")
    }

    fun getList(filter: (T) -> Boolean): List<T> {
        return repo.values.filter(filter)
    }

    fun getPaginatedList(pagination: Pagination? = null, filter: (T) -> Boolean): PaginatedResult<T> {
        val list = repo.values.asSequence().filter(filter)
        return if (pagination != null) {
            val skipItems = pagination.page * pagination.limit
            val filteredItems = if (list.count() <= skipItems) {
                listOf()
            } else {
                list.drop(skipItems).toList()
            }
            PaginatedResult(
                list = filteredItems,
                page = pagination.page,
                limit = pagination.limit,
                total = repo.size
            )
        } else {
            PaginatedResult(
                list = list.toList(),
                page = 0,
                limit = list.count(),
                total = repo.size
            )
        }
    }

    fun save(id: ID, value: T): T {
        repo[id] = value
        return value
    }

    fun delete(id: ID) {
        repo.remove(id)
    }
}

class GenericRepositoryMockDelegate<ID : Any, T : Any>(
    val initialRepo: Map<ID, T>?,
    val name: String?
) {
    private var repo: GenericRepositoryMock<ID, T>? = null
    operator fun getValue(thisRef: Any, property: KProperty<*>): GenericRepositoryMock<ID, T> {
        return repo ?: GenericRepositoryMock(name ?: property.name, initialRepo ?: mapOf()).also {
            repo = it
        }
    }
}

fun <ID : Any, T : Any> repo(name: String? = null, initialRepoProvider: (() -> Map<ID, T>?)? = null) =
    GenericRepositoryMockDelegate<ID, T>(initialRepoProvider?.let { it() }, name)