package dev.limebeck.libs.models

inline fun <reified T> tryOrNull(block: () -> T): T? {
    return try {
        block()
    } catch (e: Throwable){
        null
    }
}
