package dev.limebeck.logger

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

actual fun logger(tag: String): Logger = NaiveJsLogger(tag)

enum class LogLevel {
    DEBUG,
    INFO,
    WARN,
    ERROR
}

var logLevel = LogLevel.INFO

class NaiveJsLogger(val tag: String) : Logger {
    private fun log(level: LogLevel, msg: String, throwable: Throwable? = null) {
        val now: Instant = Clock.System.now()
        console.log("$now [$level] ($tag): $msg]", throwable ?: undefined)
    }

    override fun debug(message: MessageProducer) {
        if (logLevel >= LogLevel.DEBUG) {
            log(LogLevel.DEBUG, message())
        }
    }

    override fun info(message: MessageProducer) {
        if (logLevel >= LogLevel.INFO) {
            log(LogLevel.INFO, message())
        }
    }

    override fun warn(message: MessageProducer) {
        if (logLevel >= LogLevel.WARN) {
            log(LogLevel.WARN, message())
        }
    }

    override fun error(throwable: Throwable?, message: MessageProducer) {
        if (logLevel >= LogLevel.ERROR) {
            log(LogLevel.ERROR, message(), throwable)
        }
    }
}