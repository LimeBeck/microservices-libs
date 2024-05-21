package dev.limebeck.logger

typealias MessageProducer = () -> String

interface Logger {
    fun debug(message: MessageProducer)
    fun info(message: MessageProducer)
    fun warn(message: MessageProducer)
    fun error(throwable: Throwable? = null, message: MessageProducer)
}

inline fun <reified T> T.logger() = logger(T::class.qualifiedName ?: T::class.simpleName!!)

expect fun logger(tag: String): Logger