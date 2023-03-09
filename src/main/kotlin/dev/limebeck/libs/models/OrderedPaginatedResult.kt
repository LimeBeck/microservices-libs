package dev.limebeck.libs.model

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