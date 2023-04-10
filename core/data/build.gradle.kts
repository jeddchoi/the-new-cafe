plugins {
    id("jeddchoi.android.library")
}

android {
    namespace = "io.github.jeddchoi.data"
}


dependencies {
    implementation(libs.kotlinx.coroutines.android)
    implementation(project(":core:model"))
    testImplementation(project(":core:testing"))
}