@file:Suppress("DSL_SCOPE_VIOLATION")

plugins {
    id("kotlin")
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.plugin.serialization)
}


dependencies {
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.kotlinx.datetime)
}