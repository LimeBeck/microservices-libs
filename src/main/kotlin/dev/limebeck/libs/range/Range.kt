package dev.limebeck.libs.range

import java.time.Instant

data class Range<T>(
        val start: T?,
        val end: T?
)

data class StrictRange<T>(
        val start: T,
        val end: T
)

fun <T : Comparable<T>> StrictRange<T>.isValueInRange(value: T): Boolean =
        start < value && value < end


fun <T : Comparable<T>> Range<T>.isValueInRange(value: T): Boolean =
        start?.let { it < value } ?: true && end?.let { it > value } ?: true


fun <T : Comparable<T>> StrictRange<T>.isIntersectWith(value: StrictRange<T>): Boolean =
        isValueInRange(value.start) || isValueInRange(value.end) || value.isValueInRange(start) || value.isValueInRange(end)


fun <T : Comparable<T>> Range<T>.isIntersectWith(value: Range<T>): Boolean =
        value.start?.let { isValueInRange(it) } ?: false
                || value.end?.let { isValueInRange(value.end) } ?: false
                || end?.let { value.isValueInRange(it) } ?: false
                || start?.let { value.isValueInRange(it) } ?: false


operator fun <T : Comparable<T>> StrictRange<T>.contains(value: T): Boolean = isValueInRange(value)
operator fun <T : Comparable<T>> Range<T>.contains(value: T): Boolean = isValueInRange(value)
operator fun <T : Comparable<T>> T?.rangeTo(value: T?): Range<T> = Range(this, value)