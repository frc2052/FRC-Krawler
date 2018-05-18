import org.gradle.kotlin.dsl.implementation
import org.jetbrains.kotlin.cli.jvm.main

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
    implementation(Config.Libs.Kotlin.jvm)
    implementation(Config.Libs.Kotlin.ktx)

    implementation(Config.Libs.Support.multidex)
    implementation(Config.Libs.Support.appCompat)
    implementation(Config.Libs.Support.v4)
    implementation(Config.Libs.Support.cardView)
    implementation(Config.Libs.Support.constraint)
    implementation(Config.Libs.Support.design)
    implementation(Config.Libs.Support.pref)

    implementation(Config.Libs.Miscellaneous.gson)
    implementation(Config.Libs.Miscellaneous.guava)
    implementation(Config.Libs.Miscellaneous.eventbus)
    implementation(Config.Libs.Miscellaneous.okhttp)
    implementation(Config.Libs.Miscellaneous.openCsv)
    implementation(Config.Libs.Miscellaneous.materialDialog)
    implementation(Config.Libs.GreenDao.greenDao)

    implementation(Config.Libs.RxJava.rxBinding)
    implementation(Config.Libs.RxJava.rxBindingv4)
    implementation(Config.Libs.RxJava.rxBindingAppCompat)
    implementation(Config.Libs.RxJava.rxBindingDesign)
    implementation(Config.Libs.RxJava.rxPermissions)

    implementation(Config.Libs.Dagger.dagger)
    kapt(Config.Libs.Dagger.daggerProcessor)

    implementation(Config.Libs.Arch.common)
    implementation(Config.Libs.Arch.extensions)

    implementation(Config.Libs.Firebase.database)
    implementation(Config.Libs.Firebase.crash)
    implementation(Config.Libs.Firebase.messaging)
    implementation(Config.Libs.FirebaseUi.database)

    implementation(Config.Libs.ButterKnife.butterKnife)
    kapt(Config.Libs.ButterKnife.butterKnifeProcessor)
    implementation(Config.Libs.Miscellaneous.smartAdapters)


    implementation(project(":core:common"))
    implementation(project(":core:data"))
    implementation(project(":core:bluetooth"))
    implementation(project(":core:metrics"))

    /*implementation(project(":core:common"))
    implementation(project(":core:bluetooth"))
    implementation(project(":core:data"))
    implementation(project(":core:metrics"))*/
}

apply(plugin = "com.google.gms.google-services")
