package dev.limebeck.libs.scopes

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

class DateBasedIdGenerator : ScopeIdGenerator {

    private var lastId = ""
    private var counter = 0

    private val lock = Mutex()

    /**
     * Generates unique identifier based on current date and time
     */
    override suspend fun generateId(): String {
        lock.withLock {
            val id = Clock.System.now().format()
            if (id != lastId) {
                counter = 0
                lastId = id
            }
            val suffix = counter
                .inc()
                .toString()
                .padStart(3, '0')
            return id + suffix
        }
    }

    private fun Instant.format() = this.toString()
        .replace("T", "")
        .replace("Z", "")
        .replace(":", "")
        .replace("-", "")
        .replace(".", "")
}