package dev.limebeck.libs.logger

import kotlin.reflect.KClass

typealias MessageProducer = () -> String

interface Logger {
    fun debug(message: MessageProducer)
    fun info(message: MessageProducer)
    fun warn(message: MessageProducer)
    fun error(throwable: Throwable? = null, message: MessageProducer)
}

expect inline fun <reified T> T.logger(): Logger

expect fun KClass<*>.logger(): Logger

expect fun logger(tag: String): Logger