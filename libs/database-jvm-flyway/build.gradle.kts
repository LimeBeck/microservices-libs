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
    api(libs.flyway.core)
    api(libs.flyway.postgresql)
    api(project(":libs:common"))
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}

sourceSets {
    test {}
}
