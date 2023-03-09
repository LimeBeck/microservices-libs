package dev.limebeck.libs.model.valueGenerator

import java.util.*

/**
 * Generate UUID String with optional dash removing
 */
class UUIDStringGenerator(private val removeDash: Boolean = true) : ValueGenerator<String> {
    override fun generate(): String {
        return UUID.randomUUID().toString().let { if (removeDash) it.replace("-", "") else it }
    }
}