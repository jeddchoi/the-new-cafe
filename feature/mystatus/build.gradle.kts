plugins {
    id("jeddchoi.android.feature")
    id("jeddchoi.android.library.compose")
}

android {
    namespace = "io.github.jeddchoi.mystatus"
}

dependencies {
    implementation(libs.kotlinx.datetime)
}
