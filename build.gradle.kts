// Top-level build file where you can add configuration options common to all sub-projects/modules.

plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.jvm) apply false
    alias(libs.plugins.hilt) apply false
    alias(libs.plugins.google.services) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.kotlin.android) apply false
    idea
}

idea {
    module {
        isDownloadSources = true
        isDownloadJavadoc = true
    }
}


// It fixes 'getLifecycle' overrides nothing in LifecycleService
configurations.all {
    resolutionStrategy {
        eachDependency {
            when (requested.module.toString()) {
                "androidx.lifecycle:lifecycle-common" -> useVersion("2.6.1")
                // ...etc
            }
        }
    }
}