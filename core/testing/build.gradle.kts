plugins {
    id("jeddchoi.android.library")
}

android {
    namespace = "io.github.jeddchoi.testing"
}

dependencies {

    api(libs.junit4)
    api(libs.androidx.test.core)

    api(libs.androidx.test.espresso.core)
    api(libs.androidx.test.runner)
    api(libs.androidx.test.rules)

}