plugins {
    id("jeddchoi.android.library")
}

android {
    namespace = "io.github.jeddchoi.domain"
}

dependencies {
    implementation(project(":core:common"))
    testImplementation(project(":core:testing"))
}