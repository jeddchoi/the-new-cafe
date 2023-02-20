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
            implementationClass = "io.github.jeddchoi.buildlogic.plugins.AndroidApplicationConventionPlugin"
        }
        register("androidLibrary") {
            id = "jeddchoi.android.library"
            implementationClass = "io.github.jeddchoi.buildlogic.plugins.AndroidLibraryConventionPlugin"
        }
        register("androidFeature") {
            id = "jeddchoi.android.feature"
            implementationClass = "io.github.jeddchoi.buildlogic.plugins.AndroidFeatureConventionPlugin"
        }
//        register("androidHilt") {
//            id = "jeddchoi.android.hilt"
//            implementationClass = "io.github.jeddchoi.buildlogic.plugins.AndroidHiltConventionPlugin"
//        }
    }
}