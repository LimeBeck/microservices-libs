package dev.limebeck.libs.logger

import kotlin.reflect.KClass

typealias MessageProducer = () -> String

interface Logger {
    fun debug(message: MessageProducer)
    fun info(message: MessageProducer)
    fun warn(message: MessageProducer)
    fun error(throwable: Throwable? = null, message: MessageProducer)
}

inline fun <reified T> T.logger() = logger(T::class.simpleName!!)
fun KClass<*>.logger() = logger(simpleName!!)

expect fun logger(tag: String): Logger