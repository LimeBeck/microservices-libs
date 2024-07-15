plugins {
    alias(libs.plugins.kotlin.multiplatform)
    id("maven-publish")
    id("signing")
    alias(libs.plugins.dokka)
//    alias(libs.plugins.ksp)
}

val libVersion: String by project
group = "dev.limebeck"
version = libVersion
repositories {
    mavenCentral()
}

val commonArtifactIdPart = "multiplatform-test-utils"
val commonnamePart = "Dev.LimeBeck Multiplatform Test Utils"
kotlin {
    metadata {
        mavenPublication {
            artifactId = "$commonArtifactIdPart"
            pom {
                name.set(commonnamePart)
                description.set("Kotlin metadata module for $commonnamePart library")
            }
        }
    }

    jvm {
        mavenPublication {
            artifactId = "$commonArtifactIdPart-jvm"
            pom {
                name.set("$commonnamePart library JVM")
                description.set("Kotlin JVM module for $commonnamePart library")
            }
        }
        compilations.all {
//            kotlinOptions.jvmTarget = "1.8"
        }
        testRuns["test"].executionTask.configure {
            useJUnitPlatform()
        }
    }

    js(IR) {
        mavenPublication {
            artifactId = "$commonArtifactIdPart-js"
            pom {
                name.set("$commonnamePart library JS")
                description.set("Kotlin JS module for $commonnamePart library")
            }
        }
        binaries.executable()
        nodejs()
    }

    val hostOs = System.getProperty("os.name")
    val isMingwX64 = hostOs.startsWith("Windows")
    val nativeTarget = when {
        hostOs == "Mac OS X" -> macosX64("native") {
            mavenPublication {
                artifactId = "$commonArtifactIdPart-native-macos"
                pom {
                    name.set("$commonnamePart library native-macos")
                    description.set("Kotlin native-macos module for $commonnamePart library")
                }
            }
        }

        hostOs == "Linux" -> linuxX64("native") {
            mavenPublication {
                artifactId = "$commonArtifactIdPart-native-linux"
                pom {
                    name.set("$commonnamePart library native-linux")
                    description.set("Kotlin native-linux module for $commonnamePart library")
                }
            }
        }

        isMingwX64 -> mingwX64("native") {
            mavenPublication {
                artifactId = "$commonArtifactIdPart-native-win"
                pom {
                    name.set("$commonnamePart library native-win")
                    description.set("Kotlin native-win module for $commonnamePart library")
                }
            }
        }

        else -> throw GradleException("Host OS is not supported in Kotlin/Native.")
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(libs.kotlin.datetime)
                implementation(libs.kotlin.coroutines)
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }
        val jvmMain by getting {
            dependencies {
            }
        }
        val jvmTest by getting {
            dependencies {
            }
        }
        val jsMain by getting {
            dependencies {
                implementation(kotlin("stdlib-js"))
            }
        }
        val jsTest by getting {
            dependencies {
                implementation(kotlin("test-js"))
            }
        }
        val nativeMain by getting
        val nativeTest by getting {
            dependencies {
            }
        }
    }
}
