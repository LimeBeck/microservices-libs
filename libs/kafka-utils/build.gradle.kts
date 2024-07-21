plugins {
    alias(libs.plugins.kotlin.jvm)
    id("maven-publish")
    id("signing")
}

val libVersion: String by project
group = "dev.limebeck"
version = libVersion

dependencies {
    api(project(":libs:common"))
    api(libs.kafka.clients)

    testImplementation(libs.testcontainers.kafka)
    testImplementation(libs.testcontainers.core)
    testImplementation(libs.testcontainers.junit)
    testImplementation(project(":libs:multiplatform-test-utils"))
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}

sourceSets {
    test {}
}
