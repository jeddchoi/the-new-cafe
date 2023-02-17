// Top-level build file where you can add configuration options common to all sub-projects/modules.

plugins {
    @Suppress("DSL_SCOPE_VIOLATION") // TODO: delete these suppress when updating Gradle 8.1+
    alias(libs.plugins.android.application) apply false
    @Suppress("DSL_SCOPE_VIOLATION")
    alias(libs.plugins.kotlin.jvm) apply false
    @Suppress("DSL_SCOPE_VIOLATION")
    alias(libs.plugins.android.library) apply false
}