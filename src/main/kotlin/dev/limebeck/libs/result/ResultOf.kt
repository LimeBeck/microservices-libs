package dev.limebeck.libs.result

sealed class ResultOf<out T, out R> {
    data class Success<T>(
        val value: T
    ) : ResultOf<T, Nothing>()

    data class Error<R>(
        val error: R
    ) : ResultOf<Nothing, R>()

    val isSuccess
        get() = this is Success

    val isError
        get() = !isSuccess

    fun successOrNull() = this as? Success<out T>
    fun errorOrNull() = this as? Error<out R>
}