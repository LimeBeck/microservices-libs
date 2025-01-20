import com.vanniktech.maven.publish.SonatypeHost
import org.jetbrains.dokka.gradle.DokkaTaskPartial

plugins {
    alias(libs.plugins.kotlin.multiplatform) apply false
    alias(libs.plugins.kotlin.jvm) apply false
    alias(libs.plugins.versions)
    alias(libs.plugins.dokka)
    alias(libs.plugins.dokka.javadoc) apply false
    alias(libs.plugins.publish)
}

repositories {
    mavenCentral()
}

val libVersion: String by project
group = "dev.limebeck.libs"
version = libVersion

subprojects {
    val subproject = this

    group = rootProject.group
    version = rootProject.version

    apply(plugin = "org.jetbrains.dokka")
//    apply(plugin = "org.jetbrains.dokka-javadoc")
    apply(plugin = "com.vanniktech.maven.publish")

    repositories {
        mavenCentral()
    }

    // configure all format tasks at once
    tasks.withType<DokkaTaskPartial>().configureEach {
        outputDirectory.set(layout.buildDirectory.dir("docs/partial"))
        dokkaSourceSets.configureEach {
            includes.from("README.MD")
        }
    }

    mavenPublishing {
        publishToMavenCentral(SonatypeHost.CENTRAL_PORTAL)
        signAllPublications()

        pom {
            url.set("https://github.com/LimeBeck/microservices-libs")
            description.set("${subproject.name} from limebeck.dev libraries")
            developers {
                developer {
                    id.set("LimeBeck")
                    name.set("Anatoly Nechay-Gumen")
                    email.set("mail@limebeck.dev")
                }
            }
            licenses {
                license {
                    name.set("MIT license")
                    url.set("https://github.com/LimeBeck/microservices-libs/blob/master/LICENCE")
                    distribution.set("repo")
                }
            }
            scm {
                connection.set("scm:git:git://github.com/LimeBeck/microservices-libs.git")
                developerConnection.set("scm:git:ssh://github.com/LimeBeck/microservices-libs.git")
                url.set("https://github.com/LimeBeck/microservices-libs")
            }
        }
    }
}

tasks.withType<DokkaTaskPartial> {
    dokkaSourceSets.configureEach {
        includes.from("README.MD")
    }
}
