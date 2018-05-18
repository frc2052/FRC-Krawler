private const val kotlinVersion = "1.2.41"

object Config {
    object Android {
        val buildToolsVersion = "27.0.3"
        val minSdkVersion = 19
        val targetSdkVersion = 27
        val compileSdkVersion = 27
        const val applicationId = "com.team2052.frckrawler"
        val versionCode = 40
        val versionName = "4.0.0"
    }

    object Plugins {
        const val android = "com.android.tools.build:gradle:3.2.0-alpha14"
        const val kotlin = "org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlinVersion"
        const val google = "com.google.gms:google-services:3.2.1"
        const val firebase = "com.google.firebase:firebase-plugins:1.1.5"
        const val greenDao = "org.greenrobot:greendao-gradle-plugin:3.2.2"
    }

    object Libs {
        object Kotlin {
            const val jvm = "org.jetbrains.kotlin:kotlin-stdlib-jdk8:$kotlinVersion"
            const val ktx = "androidx.core:core-ktx:0.3"
        }

        object Support {
            private const val version = "27.1.1"

            const val multidex = "com.android.support:multidex:1.0.3"
            const val v4 = "com.android.support:support-v4:$version"
            const val appCompat = "com.android.support:appcompat-v7:$version"
            const val design = "com.android.support:design:$version"
            const val cardView = "com.android.support:cardview-v7:$version"
            const val pref = "com.android.support:preference-v7:$version"

            const val constraint = "com.android.support.constraint:constraint-layout:1.1.0"
        }

        object Arch {
            private const val version = "1.1.1"

            const val common = "android.arch.lifecycle:common-java8:$version"
            const val extensions = "android.arch.lifecycle:extensions:$version"
        }

        object GreenDao {
            const val version = "3.2.2"
            const val greenDao = "org.greenrobot:greendao:$version"
        }

        object Firebase {
            const val database = "com.google.firebase:firebase-database:15.0.0"
            const val crash = "com.google.firebase:firebase-crash:15.0.2"
            const val messaging = "com.google.firebase:firebase-messaging:15.0.2"
        }

        object FirebaseUi {
            const val database = "com.firebaseui:firebase-ui-database:3.3.1"
        }

        object RxJava {
            const val bindingVersion = "1.0.1"

            const val rxAndroid = "io.reactivex:rxandroid:1.2.1"
            const val rxJava = "io.reactivex:rxjava:1.1.6"

            const val rxBinding = "com.jakewharton.rxbinding:rxbinding:$bindingVersion"
            const val rxBindingv4 = "com.jakewharton.rxbinding:rxbinding-support-v4:$bindingVersion"
            const val rxBindingAppCompat = "com.jakewharton.rxbinding:rxbinding-appcompat-v7:$bindingVersion"
            const val rxBindingDesign = "com.jakewharton.rxbinding:rxbinding-design:$bindingVersion"
            const val rxPermissions = "com.tbruyelle.rxpermissions:rxpermissions:0.7.0@aar"
        }

        object Dagger {
            const val dagger = "com.google.dagger:dagger:2.6.1"
            const val daggerProcessor = "com.google.dagger:dagger-compiler:2.6.1"
        }

        object ButterKnife {
            const val butterKnife = "com.jakewharton:butterknife:8.0.1"
            const val butterKnifeProcessor = "com.jakewharton:butterknife-compiler:8.0.1"
        }

        object Miscellaneous {
            const val gson = "com.google.code.gson:gson:2.8.4"
            const val guava = "com.google.guava:guava:25.0-android"

            const val okhttp = "com.squareup.okhttp3:okhttp:3.10.0"
            const val eventbus = "org.greenrobot:eventbus:3.1.1"
            const val smartAdapters = "io.nlopez.smartadapters:library:1.3.1"
            const val openCsv = "net.sf.opencsv:opencsv:2.3"
            const val materialDialog = "com.afollestad.material-dialogs:core:0.9.6.0"
        }
    }
}