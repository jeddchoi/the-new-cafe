plugins {
    id("jeddchoi.android.feature")
    id("jeddchoi.android.library.compose")
}

android {
    namespace = "io.github.jeddchoi.authentication"
}

dependencies {
    implementation(libs.timber)
    implementation(project(":core:common"))
}
