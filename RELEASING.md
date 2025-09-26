# Releasing the app
 1. Bump the version code and version name in `/app/build.gradle`.
 2. Run `./gradlew :app:generateBaselineProfile` to generate a [baseline profile](https://developer.android.com/topic/performance/baselineprofiles/overview)
 3. In Android Studio go to Build > Generate Signed App Bundle or APK
     * Choose "App Bundle"
     * Enter our keystore information
     * Choose the "release" build variant