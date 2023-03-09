package dev.limebeck.libs.database

import javax.sql.DataSource

interface DataSourceProvider {
    fun getDataSource(
        dbUrl: String,
        dbDriver: String,
        dbUsername: String,
        dbPassword: String,
        dbMaxPoolSize: Int = 10
    ): DataSource
}