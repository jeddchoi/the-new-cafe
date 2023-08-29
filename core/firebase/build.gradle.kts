@Suppress("DSL_SCOPE_VIOLATION") // TODO: Remove once KTIJ-19369 is fixed
plugins {
    id("jeddchoi.android.library")
    id("jeddchoi.android.hilt")
    alias(libs.plugins.plugin.serialization)

}

android {
    namespace = "io.github.jeddchoi.firebase"
}

dependencies {
    implementation(project(":core:model"))
    implementation(project(":core:common"))
    implementation(project(":core:data"))

    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.kotlinx.coroutines.play.services) // Source: https://github.com/Kotlin/kotlinx.coroutines/tree/master/integration/kotlinx-coroutines-play-services
    implementation(libs.androidx.paging.compose)
    implementation(libs.kotlinx.datetime)
    implementation(libs.kotlinx.serialization.json)

    /**
     * Firebase dependencies
     */

    implementation(platform(libs.firebase.bom))
    // Add the dependency for the Cloud Functions library
    implementation(libs.firebase.functions.ktx)
    // Declare the dependency for the Cloud Firestore library
    implementation(libs.firebase.firestore.ktx)
    // Add the dependency for the Realtime Database library
    implementation(libs.firebase.database.ktx)
    // Add the dependency for the Firebase Authentication library
    implementation(libs.firebase.auth.ktx)
    // Add the dependencies for the Remote Config and Analytics libraries
    implementation(libs.firebase.config.ktx)
    implementation(libs.firebase.analytics.ktx)
}