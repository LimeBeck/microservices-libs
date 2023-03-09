package logger

import dev.limebeck.libs.logger.getLogger
import org.junit.jupiter.api.Test

class LoggerTest {
    companion object {
        val logger = getLogger()
    }

    @Test
    fun log(){
        logger.debug("<417266> To log something")
    }
}