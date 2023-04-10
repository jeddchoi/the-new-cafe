package io.github.jeddchoi.ui.feature

import androidx.core.net.toUri
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavDeepLink
import io.github.jeddchoi.designsystem.Icon


interface AppNavigation {
    val titleTextId: Int

    // routing
    val name: String
    fun route(arg: String? = null): String
    val arguments: List<NamedNavArgument>
    val deepLinks: List<NavDeepLink>

    companion object {
        val baseWebUri = "https://io.github.jeddchoi.thenewcafe".toUri()
        val baseAppUri = "app://io.github.jeddchoi.thenewcafe".toUri()

    }
}


interface BottomNavigation : AppNavigation {
    val selectedIcon: Icon
    val unselectedIcon: Icon
    val iconTextId: Int
}

interface GraphStartNavigation {
    val routeGraph: String
}