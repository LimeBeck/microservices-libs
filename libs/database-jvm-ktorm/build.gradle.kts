plugins {
    alias(libs.plugins.kotlin.jvm)
    id("maven-publish")
    id("signing")
    alias(libs.plugins.dokka)
}

val libVersion: String by project
group = "dev.limebeck"
version = libVersion

tasks.dokkaHtmlPartial {
    outputDirectory.set(layout.buildDirectory.dir("docs/partial"))
}

dependencies {
    implementation(kotlin("reflect"))
    api(libs.ktorm)
    api(project(":libs:common"))
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}

sourceSets {
    test {}
}
