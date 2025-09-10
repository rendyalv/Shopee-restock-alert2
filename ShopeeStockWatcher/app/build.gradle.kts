plugins {
    id 'com.android.application'
    id 'org.jetbrains.kotlin.android'
}

android {
    namespace "com.example.shopeewatcher"
    compileSdk 34

    defaultConfig {
        applicationId "com.example.shopeewatcher"
        minSdk 24
        targetSdk 34
        versionCode 1
        versionName "1.0"
    }

    buildFeatures {
        compose true
    }

    composeOptions {
        kotlinCompilerExtensionVersion "1.5.7"
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {
    // Jetpack Compose
    implementation "androidx.activity:activity-compose:1.8.2"
    implementation "androidx.compose.ui:ui:1.6.0"
    implementation "androidx.compose.material:material:1.6.0"
    implementation "androidx.compose.ui:ui-tooling-preview:1.6.0"

    // WorkManager
    implementation "androidx.work:work-runtime-ktx:2.9.0"

    // Timber logging
    implementation "com.jakewharton.timber:timber:5.0.1"

    // Notifications & Core KTX
    implementation "androidx.core:core-ktx:1.12.0"

    // Compose preview tooling
    debugImplementation "androidx.compose.ui:ui-tooling:1.6.0"
}

