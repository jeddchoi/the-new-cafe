package io.github.jeddchoi.ui.feature

import androidx.navigation.NamedNavArgument
import androidx.navigation.NavDeepLink
import io.github.jeddchoi.designsystem.Icon


interface AppNavigation {
    val selectedIcon: Icon
    val unselectedIcon: Icon
    val iconTextId: Int
    val titleTextId: Int

    fun route(arg: String? = null): String
    val arguments: List<NamedNavArgument>
    val deepLinks: List<NavDeepLink>
}

//interface RallyDestination {
//    val icon: ImageVector
//    val route: String
//}

//enum class TopLevelDestination(
//    val selectedIcon: Icon,
//    val unselectedIcon: Icon,
//    val iconTextId: Int,
//    val titleTextId: Int,
//)