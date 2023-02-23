package io.github.jeddchoi.ui.feature

import android.graphics.drawable.Icon



interface AppNavigation {
    val route: String
    val selectedIcon : Icon
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