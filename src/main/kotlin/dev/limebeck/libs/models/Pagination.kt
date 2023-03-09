package dev.limebeck.libs.models

import dev.limebeck.libs.errors.InvalidParameterException


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