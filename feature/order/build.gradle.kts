plugins {
    id("jeddchoi.android.feature")
    id("jeddchoi.android.library.compose")
}

android {
    namespace = "io.github.jeddchoi.order"
}

dependencies {
    implementation(libs.accompanist.permissions)
    implementation(libs.coil.compose)
    implementation(libs.kotlinx.datetime)
}