plugins {
    id("jeddchoi.android.library")
}

android {
    namespace = "io.github.jeddchoi.data"
}


dependencies {
    testImplementation(project(":core:testing"))
}