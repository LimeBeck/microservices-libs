package dev.limebeck.libs.time

import java.time.Duration
import java.time.Instant

interface TimeProvider {
    fun getCurrentTime(): Instant
}

object RealTimeProvider : TimeProvider {
    override fun getCurrentTime(): Instant = Instant.now()
}

class TimeProviderMock(initialTime: Instant) : TimeProvider {
    var time = initialTime
    override fun getCurrentTime(): Instant {
        return time
    }
}