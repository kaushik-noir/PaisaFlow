import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
}

/**
 * Backend URL.
 *
 * Default:
 *   Android Emulator -> host machine localhost
 *   http://10.0.2.2:8000/
 *
 * You can override it without editing this file:
 *
 * gradle.properties:
 *   PAISAFLOW_API_BASE_URL=http://192.168.1.10:8000/
 *
 * or for production:
 *   PAISAFLOW_API_BASE_URL=https://your-production-api.example/
 *
 * Never place passwords, API secrets, JWT signing keys, database
 * credentials, or provider private keys here. BuildConfig values can
 * be extracted from the APK.
 */
val paisaFlowApiBaseUrl = providers
    .gradleProperty("PAISAFLOW_API_BASE_URL")
    .orElse("http://10.0.2.2:8000/")
    .get()
    .let { url ->
        if (url.endsWith("/")) url else "$url/"
    }

android {
    namespace = "com.paisaflow.app"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.paisaflow.app"

        minSdk = 26
        targetSdk = 34

        versionCode = 1
        versionName = "1.0"

        buildConfigField(
            "String",
            "API_BASE_URL",
            "\"$paisaFlowApiBaseUrl\"",
        )
    }

    buildTypes {
        debug {
            isMinifyEnabled = false
        }

        release {
            isMinifyEnabled = true
            isDebuggable = false

            proguardFiles(
                getDefaultProguardFile(
                    "proguard-android-optimize.txt",
                ),
                "proguard-rules.pro",
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }
}

/**
 * Kotlin's modern compiler-options DSL.
 * Keep this aligned with compileOptions above.
 */
kotlin {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_17)
    }
}

dependencies {

    // ---------------------------------------------------------------------
    // Jetpack Compose
    // ---------------------------------------------------------------------

    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)

    implementation(libs.androidx.material3)
    implementation(libs.androidx.material.icons.extended)

    // ---------------------------------------------------------------------
    // Networking
    // ---------------------------------------------------------------------

    implementation(libs.retrofit)
    implementation(libs.retrofit.kotlinx.serialization)
    implementation(libs.okhttp.logging)

    // ---------------------------------------------------------------------
    // Kotlin serialization
    // ---------------------------------------------------------------------

    implementation(libs.kotlinx.serialization.json)

    // ---------------------------------------------------------------------
    // Coroutines
    // ---------------------------------------------------------------------

    implementation(libs.kotlinx.coroutines.android)

    // ---------------------------------------------------------------------
    // Biometrics / FragmentActivity
    // ---------------------------------------------------------------------

    implementation(libs.androidx.biometric)
    implementation(libs.androidx.fragment.ktx)

    // ---------------------------------------------------------------------
    // Debug tooling
    // ---------------------------------------------------------------------

    debugImplementation(libs.androidx.ui.tooling)
}
