package dev.limebeck.libs.valueGenerator

import kotlin.random.Random

/**
 * Generate random string from specified char pool with specified length
 */
class SimpleRandomStringGenerator(
        private val length: Int = 10,
        private val charPool: List<Char> = (('1'..'9') + ('A'..'Z') + ('a'..'z')).toList()
) : ValueGenerator<String> {
    override fun generate(): String {
        return (1..length)
                .map { _ -> Random.nextInt(0, charPool.size) }
                .map(charPool::get)
                .joinToString("")
    }
}