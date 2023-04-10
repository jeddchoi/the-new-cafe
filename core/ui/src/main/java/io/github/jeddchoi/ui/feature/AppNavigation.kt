package io.github.jeddchoi.ui.feature

import androidx.navigation.NamedNavArgument
import androidx.navigation.NavDeepLink
import io.github.jeddchoi.designsystem.Icon

const val baseWebUri = "https://www.example.com"
const val baseAppUri = "jeddchoi://thenewcafe"

interface AppNavigation {
    val titleTextId: Int

    // routing
    val name: String
    fun route(arg: String? = null): String
    val arguments: List<NamedNavArgument>
    val deepLinks: List<NavDeepLink>
}


interface BottomNavigation : AppNavigation {
    val selectedIcon: Icon
    val unselectedIcon: Icon
    val iconTextId: Int
}

interface GraphStartNavigation {
    val routeGraph: String
}