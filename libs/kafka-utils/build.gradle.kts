plugins {
    alias(libs.plugins.kotlin.jvm)
}

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

mavenPublishing {
    pom {
        name = "Dev.LimeBeck Utils Kafka"
    }
}
