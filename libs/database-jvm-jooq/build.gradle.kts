plugins {
    alias(libs.plugins.kotlin.jvm)
}

dependencies {
    implementation(kotlin("reflect"))
    api(project(":libs:common"))
    api(libs.jooq)

    testImplementation(kotlin("test"))
    testImplementation(project(":libs:database-jvm-flyway"))
    testImplementation(libs.h2)
    testImplementation(libs.testcontainers.postgres)
    testImplementation(libs.testcontainers.core)
    testImplementation(libs.testcontainers.junit)
}

tasks.test {
    useJUnitPlatform()
}

sourceSets {
    test {}
}

mavenPublishing {
    pom {
        name = "Dev.LimeBeck Utils Database JOOQ"
    }
}
