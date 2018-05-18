plugins {
    id("com.android.application")
    id("kotlin-android-extensions")
    kotlin("android")
    kotlin("kapt")
}

android {
    compileSdkVersion(Config.Android.compileSdkVersion)
    buildToolsVersion(Config.Android.buildToolsVersion)

    defaultConfig {
        applicationId = Config.Android.applicationId
        minSdkVersion(Config.Android.minSdkVersion)
        targetSdkVersion(Config.Android.targetSdkVersion)
        versionCode = Config.Android.versionCode
        versionName = Config.Android.versionName
        multiDexEnabled = true
        vectorDrawables.useSupportLibrary = true
    }

    compileOptions {
        setSourceCompatibility(JavaVersion.VERSION_1_8)
        setTargetCompatibility(JavaVersion.VERSION_1_8)
    }

    buildTypes {
        getByName("debug") {
            applicationIdSuffix = ".development"
        }
    }
}

dependencies {
    implementation(Config.Libs.Support.appCompat)
    implementation(Config.Libs.Support.v4)
    implementation(Config.Libs.Support.cardView)

    implementation("com.google.android.things:androidthings:1.0")
    implementation(project(":core:common"))
    implementation(project(":core:data"))
    implementation(project(":core:bluetooth"))
    implementation(project(":core:metrics"))
}