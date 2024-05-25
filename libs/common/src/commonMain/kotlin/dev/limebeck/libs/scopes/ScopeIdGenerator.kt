package dev.limebeck.libs.scopes

interface ScopeIdGenerator {

    /**
     * Generates unique identifier
     */
    suspend fun generateId(): String
}
