plugins {
    id("jeddchoi.android.feature")
    id("jeddchoi.android.library.compose")
}

android {
    namespace = "io.github.jeddchoi.order"
}

dependencies {
    implementation(libs.timber)
    implementation(libs.accompanist.permissions)

    implementation(project(":core:common"))
    implementation(libs.coil.compose)
}