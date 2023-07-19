@file:Suppress("UnstableApiUsage")

import io.github.jeddchoi.buildlogic.AppBuildType

plugins {
    id("jeddchoi.android.application")
    id("jeddchoi.android.application.compose")
    id("jeddchoi.android.hilt")
    id("com.google.gms.google-services") // for firebase
}

android {
    namespace = "io.github.jeddchoi.thenewcafe"

    defaultConfig {
        applicationId = "io.github.jeddchoi.thenewcafe"
        versionCode = 1
        versionName = "0.0.1"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }
    packagingOptions {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildTypes {
        getByName("release") {
            signingConfig = signingConfigs.getByName("debug")
        }
    }

    @Suppress("UNUSED_VARIABLE")
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

    implementation(project(":feature:profile"))
    implementation(project(":feature:mypage"))
    implementation(project(":feature:order"))
    implementation(project(":feature:authentication"))
    implementation(project(":feature:historydetail"))


    implementation(project(":core:ui"))
    implementation(project(":core:designsystem"))
    implementation(project(":core:data"))
    implementation(project(":core:common"))


    implementation(libs.timber)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.hilt.navigation.compose)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.compose.material3.windowSizeClass)

    testImplementation(kotlin("test"))
    testImplementation(project(":core:testing"))
    androidTestImplementation(kotlin("test"))
    androidTestImplementation(project(":core:testing"))
}

// TODO: Investigate this, or delete it
// androidx.test is forcing JUnit, 4.12. This forces it to use 4.13
//configurations.configureEach {
//    resolutionStrategy {
//        force(libs.junit4)
//        // Temporary workaround for https://issuetracker.google.com/174733673
//        force("org.objenesis:objenesis:2.6")
//    }
//}
