package dev.limebeck.libs.database

interface MigrationContext {
    companion object {
        var instance: MigrationContext? = null
    }
}

interface MigrationService {
    fun migrate(configureContext: ((MigrationContext) -> Unit)? = null)
}