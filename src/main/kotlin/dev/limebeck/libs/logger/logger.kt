package dev.limebeck.libs.logger

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import kotlin.reflect.KClass

inline fun <reified T : Any> T.getLogger(): Logger {
    val clazz = T::class
    return if (clazz.isCompanion) {
        LoggerFactory.getLogger(clazz.java.declaringClass)
    } else {
        LoggerFactory.getLogger(clazz.java)
    }
}

val KClass<*>.logger: Logger
    get() = LoggerFactory.getLogger(this.java)

fun Logger.error(e: Throwable, generator: () -> String) {
    if (isErrorEnabled) error(generator(), e)
}

fun Logger.error(generator: () -> String) {
    if (isErrorEnabled) error(generator())
}

fun Logger.warn(generator: () -> String) {
    if (isWarnEnabled) warn(generator())
}

fun Logger.info(generator: () -> String) {
    if (isInfoEnabled) info(generator())
}

fun Logger.debug(generator: () -> String) {
    if (isDebugEnabled) debug(generator())
}