package dev.limebeck.libs.logger

import kotlin.reflect.KClass
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

actual fun logger(tag: String): Logger = NaiveJsLogger(tag)

actual var logLevel = LogLevel.INFO

class NaiveJsLogger(val tag: String) : NaiveLogger() {
    @OptIn(ExperimentalTime::class)
    override fun log(level: LogLevel, msg: String, throwable: Throwable?) {
        val now: Instant = Clock.System.now()
        console.log("$now [$level] ($tag): $msg]", throwable ?: undefined)
    }
}

actual inline fun <reified T> T.logger() = logger(T::class.simpleName!!)
actual fun KClass<*>.logger() = logger(simpleName!!)