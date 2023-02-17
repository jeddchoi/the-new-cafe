
import io.github.jeddchoi.buildlogic.AppBuildType

plugins {
    id("jeddchoi.android.application")
}

android {
    namespace = "io.github.jeddchoi.thenewcafe"

    defaultConfig {
        applicationId = "io.github.jeddchoi.thenewcafe"
        versionCode = 1
        versionName = "0.0.1"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        val debug by getting {
            applicationIdSuffix = AppBuildType.DEBUG.applicationIdSuffix
        }
        val release by getting {
            isMinifyEnabled = true
            applicationIdSuffix = AppBuildType.RELEASE.applicationIdSuffix
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    androidTestImplementation(kotlin("test"))
}

// androidx.test is forcing JUnit, 4.12. This forces it to use 4.13
//configurations.configureEach {
//    resolutionStrategy {
//        force(libs.junit4)
//        // Temporary workaround for https://issuetracker.google.com/174733673
//        force("org.objenesis:objenesis:2.6")
//    }
//}
