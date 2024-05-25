package dev.limebeck.libs.time

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

interface TimeProvider {
    fun getCurrentTime(): Instant
}

object RealTimeProvider : TimeProvider {
    override fun getCurrentTime(): Instant = Clock.System.now()
}

class TimeProviderMock(initialTime: Instant) : TimeProvider {
    var time = initialTime
    override fun getCurrentTime(): Instant {
        return time
    }
}