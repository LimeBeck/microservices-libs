package dev.limebeck.libs.valueGenerator

interface ValueGenerator<T> {
    /**
     * Generate single value
     */
    fun generate(): T

    /**
     * Generate value satisfying the compliance condition
     */
    fun generate(complianceCondition: (T) -> Boolean): T {
        return getSequence().first(complianceCondition)
    }

    /**
     * Generate n values satisfying the compliance condition
     */
    fun generate(complianceCondition: (T) -> Boolean, number: Int): List<T> {
        return getSequence().filter(complianceCondition).take(number).toList()
    }

    /**
     * Generate n values
     */
    fun generate(number: Int): List<T> {
        return getSequence().take(number).toList()
    }

    /**
     * Lazy value generator
     */
    fun getSequence(): Sequence<T> {
        return sequence {
            while (true)
                yield(generate())
        }
    }
}

