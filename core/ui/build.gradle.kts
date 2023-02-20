plugins {
    id("jeddchoi.android.library")
    id("jeddchoi.android.library.compose")
}

android {
    namespace = "io.github.jeddchoi.ui"
}

dependencies {
    implementation(libs.kotlinx.coroutines.android)
    androidTestImplementation(project(":core:testing"))
}