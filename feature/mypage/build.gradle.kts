plugins {
    id("jeddchoi.android.feature")
    id("jeddchoi.android.library.compose")
}

android {
    namespace = "io.github.jeddchoi.mypage"
}

dependencies {
    implementation(project(":core:designsystem"))
    implementation(libs.accompanist.pager)
}