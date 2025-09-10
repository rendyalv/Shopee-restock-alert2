plugins {
    id("com.android.application")
    kotlin("android")
}

android {
    namespace = "com.example.shopeestockwatcher" // 🔹 change to your actual package name from AndroidManifest.xml
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.shopeestockwatcher" // 🔹 must match your app’s package
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
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
}

dependencies {
    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.12.0")

    // (add any other libraries you use here)
}
