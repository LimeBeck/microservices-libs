plugins {
    alias(libs.plugins.kotlin.jvm)
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

mavenPublishing {
    pom {
        name = "Dev.LimeBeck Utils Database Ktorm"
    }
}
