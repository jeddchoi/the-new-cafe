plugins {
    id("jeddchoi.android.library")
    id("jeddchoi.android.library.compose")
}

android {
    namespace = "io.github.jeddchoi.designsystem"
}

dependencies {
    androidTestImplementation(project(":core:testing"))
}