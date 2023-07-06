plugins {
    id("jeddchoi.android.library")
    id("jeddchoi.android.library.compose")
}

android {
    namespace = "io.github.jeddchoi.ui"
}

dependencies {
    implementation(project(":core:common"))
    implementation(project(":core:designsystem"))
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.androidx.navigation.common)
    androidTestImplementation(project(":core:testing"))
}