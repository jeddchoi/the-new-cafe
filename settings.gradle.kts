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
    }
}
rootProject.name = "The New Cafe"
include(":app")

include(":feature:seats")
include(":feature:stores")
include(":feature:actionlog")
include(":feature:mystatus")
include(":feature:account")

include(":core:ui")
include(":core:data")
include(":core:designsystem")
include(":core:domain")
include(":core:model")
include(":core:testing")