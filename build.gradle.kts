plugins {
    alias(libs.plugins.kotlin.multiplatform) apply false
    alias(libs.plugins.kotlin.jvm) apply false
    alias(libs.plugins.versions)
    alias(libs.plugins.dokka)
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

    mavenPublishing {
        publishToMavenCentral()
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

dokka {
    moduleName.set("Microservices Libs")

    dokkaPublications.html {
        suppressInheritedMembers.set(true)
        failOnWarning.set(true)
    }

    dokkaSourceSets.configureEach {
        includes.from("README.MD")
    }

    pluginsConfiguration.html {
        footerMessage.set("(c) LimeBeck.Dev")
    }
}

dependencies {
    dokka(project(":libs:common"))
    dokka(project(":libs:database-jvm-flyway"))
    dokka(project(":libs:database-jvm-jooq"))
    dokka(project(":libs:database-jvm-ktorm"))
    dokka(project(":libs:kafka-utils"))
    dokka(project(":libs:multiplatform-test-utils"))
}
