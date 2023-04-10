package io.github.jeddchoi.buildlogic.plugins

import com.android.build.api.dsl.ApplicationExtension
import io.github.jeddchoi.buildlogic.configureFlavors
import io.github.jeddchoi.buildlogic.configureKotlinAndroid
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.getByType

class AndroidApplicationConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("com.android.application")
                apply("org.jetbrains.kotlin.android")
            }

            extensions.configure<ApplicationExtension> {
                configureKotlinAndroid(this)
                defaultConfig.targetSdk = 33
                configureFlavors(this)
            }

            // TODO: investigate this
//            extensions.configure<ApplicationAndroidComponentsExtension> {
//                configurePrintApksTask(this)
//            }

            val libs = extensions.getByType<VersionCatalogsExtension>().named("libs")
            dependencies {
                add("implementation", libs.findLibrary("androidx.core.splashscreen").get())
            }
        }
    }

}