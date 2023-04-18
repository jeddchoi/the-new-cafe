plugins {
    id("jeddchoi.android.library")
    id("jeddchoi.android.hilt")
}

android {
    namespace = "io.github.jeddchoi.firebase"

}

dependencies {
    implementation(platform(libs.firebase.bom))
    // Add the dependency for the Cloud Functions library
    // When using the BoM, you don't specify versions in Firebase library dependencies
    implementation(libs.firebase.functions.ktx)
}