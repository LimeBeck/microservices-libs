package dev.limebeck.libs.models

inline fun <T, V> T.applyNotNull(value: V?, block: T.(value: V) -> Unit): T {
    if (value != null)
        block(value)
    return this
}
