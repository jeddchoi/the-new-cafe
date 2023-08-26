@Suppress("DSL_SCOPE_VIOLATION") // TODO: Remove once KTIJ-19369 is fixed
plugins {
    id("jeddchoi.android.library")
    id("jeddchoi.android.hilt")
}

android {
    namespace = "io.github.jeddchoi.ble"
}

dependencies {
    implementation(project(":core:model"))

    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.kotlinx.datetime)

    implementation(libs.kable)
}