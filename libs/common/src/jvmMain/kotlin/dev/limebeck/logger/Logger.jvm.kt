package dev.limebeck.logger

import kotlin.reflect.KClass
import org.slf4j.Logger as Slf4jLogger
import org.slf4j.LoggerFactory as Slf4jLoggerFactory

actual fun logger(tag: String): Logger = Slf4jLoggerWrapper(tag)

class Slf4jLoggerWrapper(val logger: Slf4jLogger) : Logger {
    constructor(tag: String) : this(Slf4jLoggerFactory.getLogger(tag))
    constructor(clazz: KClass<*>) : this(Slf4jLoggerFactory.getLogger(clazz.java))

    override fun debug(message: MessageProducer) {
        if (logger.isDebugEnabled) {
            logger.debug(message())
        }
    }

    override fun info(message: MessageProducer) {
        if (logger.isInfoEnabled) {
            logger.info(message())
        }
    }

    override fun warn(message: MessageProducer) {
        if (logger.isWarnEnabled) {
            logger.warn(message())
        }
    }

    override fun error(throwable: Throwable?, message: MessageProducer) {
        if (logger.isErrorEnabled) {
            logger.error(message(), throwable)
        }
    }
}