plugins {
    `kotlin-dsl`
}
group = "io.github.jeddchoi.buildlogic"

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
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
        register("androidApplicationCompose") {
            id = "jeddchoi.android.application.compose"
            implementationClass = "io.github.jeddchoi.buildlogic.plugins.AndroidApplicationComposeConventionPlugin"
        }
        register("androidLibrary") {
            id = "jeddchoi.android.library"
            implementationClass = "io.github.jeddchoi.buildlogic.plugins.AndroidLibraryConventionPlugin"
        }
        register("androidLibraryCompose") {
            id = "jeddchoi.android.library.compose"
            implementationClass = "io.github.jeddchoi.buildlogic.plugins.AndroidLibraryComposeConventionPlugin"
        }

        register("androidFeature") {
            id = "jeddchoi.android.feature"
            implementationClass = "io.github.jeddchoi.buildlogic.plugins.AndroidFeatureConventionPlugin"
        }
        register("androidHilt") {
            id = "jeddchoi.android.hilt"
            implementationClass = "io.github.jeddchoi.buildlogic.plugins.AndroidHiltConventionPlugin"
        }
    }
}