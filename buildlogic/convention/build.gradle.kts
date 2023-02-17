plugins {
    `kotlin-dsl`
}
group = "io.github.jeddchoi.buildlogic"

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

dependencies {
    compileOnly(libs.android.gradlePlugin)
    compileOnly(libs.kotlin.gradlePlugin)
}

gradlePlugin {
    plugins {
        register("androidApplication") {
            id = "jeddchoi.android.application"
            implementationClass = "AndroidApplicationConventionPlugin"
        }
    }
}