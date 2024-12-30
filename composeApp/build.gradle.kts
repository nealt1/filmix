import org.jetbrains.compose.ExperimentalComposeLibrary
import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.kotlinxSerialization)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsCompose)
}

kotlin {
    androidTarget {
        compilations.all {
            kotlinOptions {
                jvmTarget = "1.8"
            }
        }
    }
    
    jvm("desktop")
    
    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "ComposeApp"
            isStatic = true
        }
    }
    
    sourceSets {
        val desktopMain by getting
        
        androidMain.dependencies {
            implementation(libs.compose.ui.tooling.preview)
            implementation(libs.androidx.activity.compose)
            implementation(libs.androidx.window)

            implementation(libs.desugar.jdk)

            implementation(libs.ktor.client.android)

            implementation(libs.media3.exoplayer)
            implementation(libs.media3.exoplayer.dash)
            implementation(libs.media3.session)
            implementation(libs.media3.ui)
        }

        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material)
            implementation(compose.material3)
            implementation(compose.ui)
            @OptIn(ExperimentalComposeLibrary::class)
            implementation(compose.components.resources)

            implementation(libs.ktor.client.core)
            implementation(libs.ktor.client.encoding)
            implementation(libs.ktor.client.content.negotiation)
            implementation(libs.ktor.serialization.json)
            implementation(libs.kamel.image)

            implementation(libs.koin.compose)

            implementation(libs.kotlinx.datetime)
            implementation(libs.kotlinx.io)

            implementation(libs.material.windowsize)

            implementation(libs.voyager.navigator)
            implementation(libs.voyager.bottom.sheet.navigator)
            implementation(libs.voyager.tab.navigator)
            implementation(libs.voyager.transitions)
            implementation(libs.voyager.koin)

            implementation(libs.qrose)

            implementation(libs.paging.compose.common)
            implementation(libs.paging.common)

            implementation(libs.settings)
        }

        commonTest {
            dependencies {
                implementation(kotlin("test"))
                implementation(libs.koin.test)
            }
        }

        desktopMain.dependencies {
            implementation(compose.desktop.currentOs)
            implementation(libs.ktor.client.java)
            implementation(libs.kotlin.coroutines.swing)
            implementation(libs.vlc4j)
        }

        iosMain.dependencies {
            implementation(libs.ktor.client.darwin)
        }
    }
}

android {
    namespace = "org.filmix.app"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    sourceSets["main"].res.srcDirs("src/androidMain/res")
    sourceSets["main"].resources.srcDirs("src/commonMain/resources")

    defaultConfig {
        applicationId = "org.filmix.app"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = 1
        versionName = "1.0"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    dependencies {
        debugImplementation(libs.compose.ui.tooling)
    }
}

compose.desktop {
    application {
        mainClass = "MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "org.filmix.app"
            packageVersion = "1.0.0"
        }
    }
}
