package dev.limebeck.libs.database

import dev.limebeck.libs.logger.getLogger
import dev.limebeck.libs.logger.info
import org.flywaydb.core.Flyway
import org.flywaydb.core.internal.jdbc.JdbcTemplate
import javax.sql.DataSource

class FlywayMigrationService(
    dbUrl: String,
    dbDriver: String,
    dbUsername: String,
    dbPassword: String,
    dbMaxPoolSize: Int = 10,
    dataSourceProvider: DataSourceProvider = PostgresDataSourceProvider,
    private val clean: Boolean = false,
    private val additionalCleanBefore: ((tmpl: JdbcTemplate) -> Unit)? = null,
    private val migrationContextCreator: (() -> MigrationContext)? = null,
) : MigrationService {
    companion object {
        private val logger = getLogger()
    }

    private val dataSource: DataSource =
        dataSourceProvider.getDataSource(dbUrl, dbDriver, dbUsername, dbPassword, dbMaxPoolSize)

    override fun migrate(configureContext: ((MigrationContext) -> Unit)?) {
        // Start the migration
        logger.debug("<6a5a2585> Apply flyway migrations")
        val flyway = Flyway.configure()
            .dataSource(dataSource)
            .cleanDisabled(!clean)
            .load()

        if (clean) {
            additionalCleanBefore?.let { cleanBlock ->
                dataSource.connection.use { connection ->
                    cleanBlock(JdbcTemplate(connection))
                }
            }
            flyway.clean()
        }

        val migrationContext = migrationContextCreator?.let { it() }?.apply { configureContext?.also { it(this) } }
        migrationContext?.let { MigrationContext.instance = it }
        try {
            val result = flyway.migrate()
            if (result.success) {
                logger.info { "<28dc3c09> ${result.migrationsExecuted} migrations successfully applied" }
            } else {
                logger.error("<26163f9d> Can`t apply migrations: \n ${result.warnings.joinToString(", ")}")
            }
        } finally {
            MigrationContext.instance = null
        }
    }
}