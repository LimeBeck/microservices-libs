plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.atomicfu)
}

kotlin {
    metadata {
        mavenPublication {
            artifactId = "common"
            pom {
                name.set("Dev.LimeBeck Utils Common")
                description.set("Kotlin metadata module for Dev.LimeBeck Utils Common library")
            }
        }
    }

    jvm {
        mavenPublication {
            artifactId = "common-jvm"
            pom {
                name.set("Dev.LimeBeck Utils Common library JVM")
                description.set("Kotlin JVM module for Dev.LimeBeck Utils Common library")
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
            artifactId = "common-js"
            pom {
                name.set("Dev.LimeBeck Utils Common library JS")
                description.set("Kotlin JS module for Dev.LimeBeck Utils Common library")
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
                artifactId = "common-native-macos"
                pom {
                    name.set("Dev.LimeBeck Utils Common library native-macos")
                    description.set("Kotlin native-macos module for Dev.LimeBeck Utils Common library")
                }
            }
        }

        hostOs == "Linux" -> linuxX64("native") {
            mavenPublication {
                artifactId = "common-native-linux"
                pom {
                    name.set("Dev.LimeBeck Utils Common library native-linux")
                    description.set("Kotlin native-linux module for Dev.LimeBeck Utils Common library")
                }
            }
        }

        isMingwX64 -> mingwX64("native") {
            mavenPublication {
                artifactId = "common-native-win"
                pom {
                    name.set("Dev.LimeBeck Utils Common library native-win")
                    description.set("Kotlin native-win module for Dev.LimeBeck Utils Common library")
                }
            }
        }

        else -> throw GradleException("Host OS is not supported in Kotlin/Native.")
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                api(libs.uuid)
                api(libs.kotlin.datetime)
                api(libs.kotlin.coroutines)
                api(libs.atomic)
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(project(":libs:multiplatform-test-utils"))
                implementation(kotlin("test"))
            }
        }
        val jvmMain by getting {
            dependencies {
                api(libs.slf4j)
                api(libs.otel.api)
                api(libs.otel.sdk)
                api(libs.hikari)
                api(libs.postgres)
                api(libs.micrometer)
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
