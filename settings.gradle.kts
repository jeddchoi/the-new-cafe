@file:Suppress("UnstableApiUsage")

pluginManagement {
    includeBuild("buildlogic")
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://jitpack.io") }
    }
}
rootProject.name = "The New Cafe"
include(":app")

include(":feature:profile")
include(":feature:order")

include(":feature:mypage")
include(":feature:authentication")
include(":feature:historydetail")

include(":core:ui")
include(":core:common")
include(":core:data")
include(":core:designsystem")
include(":core:domain")
include(":core:model")
include(":core:testing")


