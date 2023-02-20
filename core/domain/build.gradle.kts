plugins {
    id("jeddchoi.android.library")
}

android {
    namespace = "io.github.jeddchoi.domain"
}

dependencies {
    testImplementation(project(":core:testing"))
}