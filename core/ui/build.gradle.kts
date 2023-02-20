plugins {
    id("jeddchoi.android.library")
    id("jeddchoi.android.library.compose")
}

android {
    namespace = "io.github.jeddchoi.ui"
}

dependencies {
    androidTestImplementation(project(":core:testing"))
}