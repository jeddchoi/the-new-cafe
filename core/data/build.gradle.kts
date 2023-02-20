plugins {
    id("jeddchoi.android.library")
}

android {
    namespace = "io.github.jeddchoi.data"
}


dependencies {
    implementation(libs.kotlinx.coroutines.android)
    testImplementation(project(":core:testing"))
}