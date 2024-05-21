rootProject.name = "microservice-utils"


pluginManagement {
    plugins {
        val kotlinVersion: String by settings
        kotlin("multiplatform") version kotlinVersion
    }
}

rootProject.projectDir.resolve("libs")
    .listFiles { it, _ -> it.isDirectory }
    ?.forEach {
        if(it.resolve("build.gradle.kts").exists()) {
            include("libs:${it.name}")
        }
    }