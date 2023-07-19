plugins {
    id("jeddchoi.android.feature")
    id("jeddchoi.android.library.compose")
}

android {
    namespace = "io.github.jeddchoi.mypage"
}

dependencies {
    implementation(libs.timber)
    implementation(project(":core:common"))
    implementation(libs.kotlinx.datetime)
    implementation(libs.androidx.compose.material3)
    implementation(libs.wheel.picker.compose)

    implementation(libs.androidx.paging.compose)
}