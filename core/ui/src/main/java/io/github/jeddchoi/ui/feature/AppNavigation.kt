package io.github.jeddchoi.ui.feature

import androidx.navigation.NamedNavArgument
import androidx.navigation.NavDeepLink
import io.github.jeddchoi.designsystem.Icon

const val baseWebUri = "https://www.example.com"
const val baseAppUri = "jeddchoi://thenewcafe"

interface AppNavigation {
    val name: String
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