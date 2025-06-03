import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.kotlinx.serialization)
    alias(libs.plugins.sqldelight)
}

kotlin {
    androidTarget {
        compilations.all {
            compileTaskProvider.configure {
                compilerOptions {
                    jvmTarget.set(JvmTarget.JVM_1_8)
                }
            }
        }
    }
    
    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach {
        it.binaries.framework {
            baseName = "shared"
            isStatic = true
        }
    }

    sourceSets {
        commonMain.dependencies {
            implementation(libs.koin.core)
            implementation(libs.kotlinx.serialization.json)
            implementation(libs.kotlinx.datetime)
            implementation("app.cash.sqldelight:runtime:2.0.2")
            // Removed Voyager dependencies temporarily due to Compose runtime conflicts
            // implementation(libs.voyager.navigator)
            // implementation(libs.voyager.tab.navigator)
            // implementation(libs.voyager.koin)
        }

        androidMain.dependencies {
            implementation(libs.sqldelight.driver.android) // Will enable later
            implementation(libs.itext.core)
            implementation(libs.itext.layout)
        }

        iosMain.dependencies {
            implementation(libs.sqldelight.driver.native) // Will enable later
        }

        commonTest.dependencies {
            implementation(libs.kotlin.test)
            implementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.3")
        }
    }
}

android {
    namespace = "com.dwyer.bandbuddy"
    compileSdk = 35
    defaultConfig {
        minSdk = 29
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

sqldelight {
    databases {
        create("BandBuddyDatabase") {
            packageName.set("com.dwyer.bandbuddy.database")
        }
    }
}
