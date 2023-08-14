plugins {
    id("jeddchoi.android.feature")
    id("jeddchoi.android.library.compose")
}

android {
    namespace = "io.github.jeddchoi.historydetail"
}

dependencies {
    implementation(libs.kotlinx.datetime)
    implementation(libs.androidx.compose.material3)

}