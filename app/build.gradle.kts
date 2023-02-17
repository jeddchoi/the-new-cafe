plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "io.github.jeddchoi.thenewcafe"
    compileSdk = 33

    defaultConfig {
        applicationId = "io.github.jeddchoi.thenewcafe"
        minSdk = 26
        targetSdk = 33
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_11.toString()
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
