package dev.limebeck.logger

import kotlin.test.Test
import kotlin.test.assertEquals

class LoggerTest {
    @Test
    fun `Test logger`() {
        val lines = mutableListOf<String>()
        val logger = NaiveNativeLogger("TestLogger", lines::add)

        logLevel = LogLevel.ERROR
        logger.debug { "debug" }
        assertEquals(0, lines.size)

        logLevel = LogLevel.DEBUG
        logger.debug { "debug" }
        assertEquals(1, lines.size)

        logger.error(Exception("<ee5de25a>")) { "error" }
        assertEquals(2, lines.size)
        lines.forEach(::println)
    }
}