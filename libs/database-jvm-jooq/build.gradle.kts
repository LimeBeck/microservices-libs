plugins {
    alias(libs.plugins.kotlin.jvm)
    id("maven-publish")
    id("signing")
}

val libVersion: String by project
group = "dev.limebeck"
version = libVersion

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
