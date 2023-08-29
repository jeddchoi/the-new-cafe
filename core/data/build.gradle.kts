@file:Suppress("DSL_SCOPE_VIOLATION")

import com.google.protobuf.gradle.id

plugins {
    id("jeddchoi.android.library")
    id("jeddchoi.android.hilt")
    alias(libs.plugins.protobuf)
}

android {
    namespace = "io.github.jeddchoi.data"
}


dependencies {
    implementation(project(":core:model"))
    implementation(project(":core:common"))
    testImplementation(project(":core:testing"))

    implementation(libs.androidx.datastore.proto)
    implementation(libs.protobuf)
    implementation(libs.androidx.paging.compose)
    implementation(libs.kotlinx.datetime)
}

protobuf {
    protoc {
        artifact = "com.google.protobuf:protoc:3.24.0"
    }
    generateProtoTasks {
        all().forEach { task ->
            task.builtins {
                id("java") {
                    option("lite")
                }
            }
        }
    }
}