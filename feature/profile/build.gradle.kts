plugins {
    id("jeddchoi.android.feature")
    id("jeddchoi.android.library.compose")
}

android {
    namespace = "io.github.jeddchoi.profile"
}

dependencies {
    implementation(libs.kotlinx.datetime)
    implementation(libs.coil.compose)
}