@file:Suppress("DSL_SCOPE_VIOLATION")

plugins {
    id("jeddchoi.android.library")
    id("jeddchoi.android.hilt")
    alias(libs.plugins.plugin.serialization)
}

android {
    namespace = "io.github.jeddchoi.data"
}


dependencies {
    implementation(libs.kotlinx.coroutines.android)
    implementation(project(":core:model"))
    testImplementation(project(":core:testing"))

    implementation(libs.kotlinx.serialization.json)
    // Source: https://github.com/Kotlin/kotlinx.coroutines/tree/master/integration/kotlinx-coroutines-play-services
    implementation(libs.kotlinx.coroutines.play.services)
    implementation(libs.kotlinx.datetime)

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
    // Add the dependencies for the Dynamic Links
    implementation(libs.firebase.dynamic.links.ktx)
}