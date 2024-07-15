package dev.limebeck.libs.db

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import dev.limebeck.libs.errors.InvalidParameterException
import org.postgresql.ds.PGSimpleDataSource
import javax.sql.DataSource

object PostgresDataSourceProvider : DataSourceProvider {
    override fun getDataSource(
        dbUrl: String,
        dbDriver: String,
        dbUsername: String,
        dbPassword: String,
        dbMaxPoolSize: Int
    ): DataSource {
        return if(dbMaxPoolSize > 1){
            val hikariConfig = HikariConfig().apply {
                jdbcUrl = dbUrl
                driverClassName = dbDriver
                username = dbUsername
                password = dbPassword
                maximumPoolSize = dbMaxPoolSize
            }

            HikariDataSource(hikariConfig)
        } else {
            when(dbDriver){
                "org.postgresql.Driver" -> {
                    PGSimpleDataSource().apply {
                        setUrl(dbUrl)
                        user = dbUsername
                        password = dbPassword
                    }
                }
                else -> throw InvalidParameterException("<c60d1fbb> Incompatible dbDriver parameter '${dbDriver}. Allowed only 'org.postgresql.Driver'")
            }
        }
    }
}