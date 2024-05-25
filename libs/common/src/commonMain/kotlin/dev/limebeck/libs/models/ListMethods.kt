package dev.limebeck.libs.models

/**
* Takes multiple nullable lists as input and returns their intersection,
 * or returns null if both are null
 */
fun <T> intersectNullableLists(vararg lists: List<T>?): List<T>? {
    if (lists.all { it == null })
        return null

    if (lists.all { it?.isEmpty() == true })
        return listOf()

    return lists.fold(listOf<T>()) { acc, list ->
        list?.let {
            if(acc.isNotEmpty())
                acc.intersect(it).toList()
            else
                it
        } ?: acc
    }
}

/**
* Takes multiple nullable lists as input and concatenates them,
 * or returns null if both are null
 */
fun <T> mergeNullableLists(vararg lists: List<T>?): List<T>? {
    if (lists.all { it == null })
        return null

    if (lists.all { it?.isEmpty() == true })
        return listOf()

    return lists.fold(listOf<T>()) { acc, list ->
        list?.let {
            acc + it
        } ?: acc
    }.toSet().toList()
}