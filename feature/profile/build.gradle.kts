plugins {
    id("jeddchoi.android.feature")
    id("jeddchoi.android.library.compose")
}

android {
    namespace = "io.github.jeddchoi.profile"
}

dependencies {
    implementation(libs.timber)
    implementation(project(":core:common"))
    implementation(libs.kotlinx.datetime)
}