plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
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

        // Android Emulator:
        // 10.0.2.2 points to your computer's localhost.
        //
        // For a physical Android phone, replace this with
        // your laptop/PC LAN IP, e.g.:
        // http://192.168.1.10:8000/
        buildConfigField(
            "String",
            "API_BASE_URL",
            "\"http://10.0.2.2:8000/\""
        )
    }

    buildTypes {
        debug {
            isMinifyEnabled = false
        }

        release {
            isMinifyEnabled = true

            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }
}

dependencies {
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)

    implementation(libs.androidx.material3)
    implementation(libs.androidx.material.icons.extended)

    // Networking
    implementation(libs.retrofit)
    implementation(libs.retrofit.kotlinx.serialization)
    implementation(libs.okhttp.logging)

    // JSON serialization
    implementation(libs.kotlinx.serialization.json)

    // Coroutines
    implementation(libs.kotlinx.coroutines.android)

    // Authentication / Biometrics
    implementation(libs.androidx.biometric)

    // AndroidX
    implementation(libs.androidx.fragment.ktx)

    // Development tools
    debugImplementation(libs.androidx.ui.tooling)
}
