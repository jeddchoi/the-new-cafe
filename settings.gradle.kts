@file:Suppress("UnstableApiUsage")

include(":feature:authentication")





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

include(":feature:account")
include(":feature:store")
include(":feature:store_list")
include(":feature:mypage")
include(":feature:actionlog")
include(":feature:mystatus")
include(":feature:authentication")


include(":core:ui")
include(":core:data")
include(":core:designsystem")
include(":core:domain")
include(":core:model")
include(":core:testing")