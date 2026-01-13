package dev.limebeck.libs.logger

enum class LogLevel {
    TRACE,
    DEBUG,
    INFO,
    WARN,
    ERROR
}

expect var logLevel: LogLevel

abstract class NaiveLogger : Logger {
    abstract fun log(level: LogLevel, msg: String, throwable: Throwable? = null)

    override fun trace(message: MessageProducer) {
        if (LogLevel.TRACE >= logLevel) {
            log(LogLevel.TRACE, message())
        }
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