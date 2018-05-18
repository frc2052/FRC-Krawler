plugins {
    id("com.android.library")
    kotlin("android")
    id("kotlin-android-extensions")
    kotlin("kapt")
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
    api(project(":core:common"))
    api(project(":core:data"))

    implementation(Config.Libs.Support.appCompat)
    implementation(Config.Libs.Support.v4)

    api(Config.Libs.GreenDao.greenDao)
    api(Config.Libs.Kotlin.jvm)
    api(Config.Libs.Dagger.dagger)
    api(Config.Libs.RxJava.rxJava)
    api(Config.Libs.RxJava.rxAndroid)
    api(Config.Libs.Miscellaneous.guava)

    api(Config.Libs.Miscellaneous.materialDialog)
    api(Config.Libs.Miscellaneous.openCsv)
}


