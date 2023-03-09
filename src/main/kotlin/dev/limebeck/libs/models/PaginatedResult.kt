package dev.limebeck.libs.model

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