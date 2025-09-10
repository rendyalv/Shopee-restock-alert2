buildscript {
    ext {
        compose_ui_version = '1.6.0'
    }
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath "com.android.tools.build:gradle:8.3.0"
        classpath "org.jetbrains.kotlin:kotlin-gradle-plugin:1.9.10"
    }
}

allprojects {
    repositories {
        google()
        mavenCentral()
    }
}
