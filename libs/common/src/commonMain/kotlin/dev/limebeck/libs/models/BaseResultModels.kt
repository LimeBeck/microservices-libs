package dev.limebeck.libs.models

import dev.limebeck.libs.errors.InvalidParameterException

open class OrderedPaginatedResult<T, R>(
    val ordering: List<R>,
    total: Int,
    limit: Int,
    page: Int,
    list: List<T>
) : PaginatedResult<T>(total, limit, page, list) {
    override fun toString(): String {
        return "OrderedPaginatedResult(ordering=$ordering,total=$total,limit=$limit,page=$page,list=$list)"
    }
}

data class Ordering<R : Enum<R>>(
    val field: R,
    val direction: Direction
) {
    enum class Direction {
        ASC,
        DESC
    }
}

open class PaginatedResult<T>(
    val total: Int,
    val limit: Int,
    val page: Int,
    val list: List<T>
) {
    override fun toString(): String {
        return "PaginatedResult(total=$total,limit=$limit,page=$page,list=$list)"
    }
}

data class Pagination(
    val limit: Int,
    val page: Int = 0
) {
    init {
        if (limit <= 0) {
            throw InvalidParameterException("<a3c84302> Pagination limit must be greater 0")
        }
        if (page < 0) {
            throw InvalidParameterException("<0ec9979e> Pagination page must be greater or equal 0")
        }
    }
}