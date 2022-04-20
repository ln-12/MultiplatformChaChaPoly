import org.jetbrains.kotlin.gradle.plugin.mpp.apple.XCFramework

plugins {
    kotlin("multiplatform")
    id("com.android.library")
    `maven-publish`
}

group = "de.tubaf.multiplatformchachapoly"
version = "1.0"

repositories {
    mavenCentral()
}

kotlin {
    android {
        publishLibraryVariants("release", "debug")
    }

    val xcf = XCFramework()
    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64(),
    ).forEach {
        it.binaries.framework {
            baseName = "shared"
            xcf.add(this)
        }

        val platform = when (it.targetName) {
            "iosX64", "iosSimulatorArm64" -> "iphonesimulator"
            "iosArm64" -> "iphoneos"
            else -> error("Unsupported target $name")
        }

        it.compilations.getByName("main") {
            cinterops.create("SwiftChachaPoly") {
                println("TASK: $interopProcessingTaskName")
                val interopTask = tasks[interopProcessingTaskName]
                println(interopTask)
                println("$rootDir/SwiftChachaPoly/build/Release-$platform/include")
                interopTask.dependsOn(":SwiftChachaPoly:build${platform.capitalize()}")
                includeDirs.headerFilterOnly("$rootDir/SwiftChachaPoly/build/Release-$platform/include")
            }
        }
    }

    sourceSets {
        val commonMain by getting
        val commonTest by getting
        val androidMain by getting {
            dependencies {
                // Crypto
                // https://github.com/google/tink
                implementation("com.google.crypto.tink:tink-android:1.6.1")
            }
        }
        val androidTest by getting
        val iosX64Main by getting
        val iosArm64Main by getting
        val iosSimulatorArm64Main by getting
        val iosMain by creating {
            dependsOn(commonMain)
            iosX64Main.dependsOn(this)
            iosArm64Main.dependsOn(this)
            iosSimulatorArm64Main.dependsOn(this)
        }
        val iosX64Test by getting
        val iosArm64Test by getting
        val iosSimulatorArm64Test by getting
        val iosTest by creating {
            dependsOn(commonTest)
            iosX64Test.dependsOn(this)
            iosArm64Test.dependsOn(this)
            iosSimulatorArm64Test.dependsOn(this)
        }
    }
}

android {
    compileSdk = 31
    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    defaultConfig {
        minSdk = 23
        targetSdk = 31
    }
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    kotlinOptions {
        jvmTarget = "1.8"
        freeCompilerArgs = listOf(
            *freeCompilerArgs.toTypedArray(),
            "-Xopt-in=kotlin.RequiresOptIn"
        )
    }
}