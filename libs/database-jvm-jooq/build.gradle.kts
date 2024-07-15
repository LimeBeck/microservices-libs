plugins {
    alias(libs.plugins.kotlin.jvm)
    id("maven-publish")
    id("signing")
    alias(libs.plugins.dokka)
}

val libVersion: String by project
group = "dev.limebeck"
version = libVersion
repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("reflect"))
    api(libs.jooq)
    api(project(":libs:common"))

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
