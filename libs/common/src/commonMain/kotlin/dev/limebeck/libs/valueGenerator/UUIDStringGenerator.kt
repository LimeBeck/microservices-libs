package dev.limebeck.libs.valueGenerator

import com.benasher44.uuid.uuid4

/**
 * Generate UUID String with optional dash removing
 */
class UUIDStringGenerator(private val removeDash: Boolean = true) : ValueGenerator<String> {
    override fun generate(): String {
        return uuid4().toString().let { if (removeDash) it.replace("-", "") else it }
    }
}