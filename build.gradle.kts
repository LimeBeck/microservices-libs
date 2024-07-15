plugins {
    alias(libs.plugins.kotlin.multiplatform) apply false
    alias(libs.plugins.kotlin.jvm) apply false
    alias(libs.plugins.versions)
}

repositories {
    mavenCentral()
}

//import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
//
//plugins {
//    kotlin("multiplatform")
//    id("java-library")
//    id("maven-publish")
//    id("signing")
//    id("org.jetbrains.dokka") version "1.8.10"
//    id("com.github.ben-manes.versions").version("0.44.0")
//}
//
//val libVersion: String by project
//group = "dev.limebeck"
//version = libVersion
//repositories {
//    mavenCentral()
//}
//
//tasks.test {
//    useJUnitPlatform()
//}
//
//tasks.withType<KotlinCompile> {
//    kotlinOptions.jvmTarget = "17"
//}
//
//sourceSets {
//    test {}
//}
//
////val stubJavaDocJar by tasks.registering(Jar::class) {
////    archiveClassifier.value("javadoc")
////}
//
//tasks.register<Jar>("dokkaHtmlJar") {
//    dependsOn(tasks.dokkaHtml)
//    from(tasks.dokkaHtml.flatMap { it.outputDirectory })
//    archiveClassifier.set("html-docs")
//}
//
//val javaDocJar by tasks.register<Jar>("dokkaJavadocJar") {
//    dependsOn(tasks.dokkaJavadoc)
//    from(tasks.dokkaJavadoc.flatMap { it.outputDirectory })
//    archiveClassifier.set("javadoc")
//}
//
//publishing {
//    repositories {
//        maven {
//            name = "MainRepo"
//            url = uri(
//                System.getenv("REPO_URI")
//                    ?: project.findProperty("repo.uri") as String
//            )
//            credentials {
//                username = System.getenv("REPO_USERNAME")
//                    ?: project.findProperty("repo.username") as String?
//                password = System.getenv("REPO_PASSWORD")
//                    ?: project.findProperty("repo.password") as String?
//            }
//        }
//    }
//
//    publications {
//        create<MavenPublication>("main") {
//            from(components["java"])
//            artifact(javaDocJar)
//            artifactId = "microservices-utils"
//            pom {
//                name.set("Microservices helpers libs")
//                description.set("Bunch of microservices helper libs")
//                groupId = "dev.limebeck"
//                url.set("https://github.com/LimeBeck/microservices-libs")
//                developers {
//                    developer {
//                        id.set("LimeBeck")
//                        name.set("Anatoly Nechay-Gumen")
//                        email.set("mail@limebeck.dev")
//                    }
//                }
//                licenses {
//                    license {
//                        name.set("MIT license")
//                        url.set("https://github.com/LimeBeck/microservices-libs/blob/master/LICENCE")
//                        distribution.set("repo")
//                    }
//                }
//                scm {
//                    connection.set("scm:git:git://github.com/LimeBeck/reveal-kt.git")
//                    developerConnection.set("scm:git:ssh://github.com/LimeBeck/reveal-kt.git")
//                    url.set("https://github.com/LimeBeck/microservices-libs")
//                }
//            }
//        }
//    }
//}
//
//signing {
//    sign(publishing.publications)
//}