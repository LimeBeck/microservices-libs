package dev.limebeck.libs.models

/**
 * Получает на вход несколько nullable списков и возвращает их пересечение, либо возвращает null,
 * если оба равны null
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
 * Получает на вход несколько nullable списков и складывает их, либо возвращает null,
 * если оба равны null
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