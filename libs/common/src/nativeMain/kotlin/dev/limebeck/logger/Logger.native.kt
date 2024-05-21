package dev.limebeck.logger

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.toKString
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import platform.posix.getenv

actual fun logger(tag: String): Logger = NaiveNativeLogger(tag)

const val LOG_LEVEL_ENV_KEY = "LOG_LEVEL"

enum class LogLevel {
    DEBUG,
    INFO,
    WARN,
    ERROR
}

@OptIn(ExperimentalForeignApi::class)
var logLevel = getenv(LOG_LEVEL_ENV_KEY)?.toKString()?.let { level ->
    LogLevel.entries.find { it.name == level.uppercase() }
} ?: LogLevel.INFO

class NaiveNativeLogger(val tag: String, val writer: (String) -> Unit = ::println) : Logger {
    private fun log(level: LogLevel, msg: String, throwable: Throwable? = null) {
        val now: Instant = Clock.System.now()
        writer("$now [$level] ($tag): $msg" + (throwable?.stackTraceToString()?.let { "\n$it" } ?: ""))
    }

    override fun debug(message: MessageProducer) {
        if (LogLevel.DEBUG >= logLevel) {
            log(LogLevel.DEBUG, message())
        }
    }

    override fun info(message: MessageProducer) {
        if (LogLevel.INFO >= logLevel) {
            log(LogLevel.INFO, message())
        }
    }

    override fun warn(message: MessageProducer) {
        if (LogLevel.WARN >= logLevel) {
            log(LogLevel.WARN, message())
        }
    }

    override fun error(throwable: Throwable?, message: MessageProducer) {
        if (LogLevel.ERROR >= logLevel) {
            log(LogLevel.ERROR, message(), throwable)
        }
    }
}