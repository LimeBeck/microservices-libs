package dev.limebeck.libs.logger

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.toKString
import platform.posix.getenv
import kotlin.reflect.KClass
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

actual fun logger(tag: String): Logger = NaiveNativeLogger(tag)

const val LOG_LEVEL_ENV_KEY = "LOG_LEVEL"

@OptIn(ExperimentalForeignApi::class)
actual var logLevel = getenv(LOG_LEVEL_ENV_KEY)?.toKString()?.let { level ->
    LogLevel.entries.find { it.name == level.uppercase() }
} ?: LogLevel.INFO

class NaiveNativeLogger(val tag: String, val writer: (String) -> Unit = ::println) : NaiveLogger() {
    @OptIn(ExperimentalTime::class)
    override fun log(level: LogLevel, msg: String, throwable: Throwable?) {
        val now: Instant = Clock.System.now()
        writer("$now [$level] ($tag): $msg" + (throwable?.stackTraceToString()?.let { "\n$it" } ?: ""))
    }
}

actual inline fun <reified T> T.logger() = logger(T::class.qualifiedName ?: T::class.simpleName!!)
actual fun KClass<*>.logger() = logger(qualifiedName ?: simpleName!!)