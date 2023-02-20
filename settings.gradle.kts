@file:Suppress("UnstableApiUsage")

include(":feature:seats")


include(":feature:stores")


include(":feature:actionlog")


include(":feature:mystatus")


include(":feature:account")


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
