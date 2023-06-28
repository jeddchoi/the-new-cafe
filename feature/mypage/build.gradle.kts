plugins {
    id("jeddchoi.android.feature")
    id("jeddchoi.android.library.compose")
}

android {
    namespace = "io.github.jeddchoi.mypage"
}

dependencies {
    implementation(project(":feature:mystatus"))
    implementation(project(":feature:actionlog"))
    implementation(libs.kotlinx.datetime)
    implementation(libs.androidx.compose.material3)
    implementation(libs.wheel.picker.compose)
}