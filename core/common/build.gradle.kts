plugins {
    id("com.android.library")
    kotlin("android")
}

android {
    compileSdkVersion(Config.Android.compileSdkVersion)
    buildToolsVersion(Config.Android.buildToolsVersion)

    defaultConfig {
        minSdkVersion(Config.Android.minSdkVersion)
        targetSdkVersion(Config.Android.targetSdkVersion)
        versionCode = Config.Android.versionCode
    }

    compileOptions {
        setSourceCompatibility(JavaVersion.VERSION_1_8)
        setTargetCompatibility(JavaVersion.VERSION_1_8)
    }
}

dependencies {
    api(Config.Libs.Kotlin.jvm)
    api(Config.Libs.Miscellaneous.okhttp)
    api(Config.Libs.Support.appCompat)
    api(Config.Libs.Miscellaneous.guava)
    api(Config.Libs.Support.design)
    api(Config.Libs.Miscellaneous.eventbus)
    api(Config.Libs.Miscellaneous.gson)

    api(Config.Libs.RxJava.rxJava)
    api(Config.Libs.RxJava.rxAndroid)
}
