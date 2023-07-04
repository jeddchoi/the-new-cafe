package io.github.jeddchoi.thenewcafe.ui.main

import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.navOptions
import io.github.jeddchoi.designsystem.CafeNavigationBar
import io.github.jeddchoi.designsystem.CafeNavigationBarItem
import io.github.jeddchoi.designsystem.Icon
import io.github.jeddchoi.mypage.navigateToMyPage
import io.github.jeddchoi.order.navigateToOrder
import io.github.jeddchoi.profile.navigateToProfile

@Composable
fun BottomBar(navController: NavHostController, currentDestination: NavDestination?) {
    val screens = listOf(MainBottomNav.Profile, MainBottomNav.Order, MainBottomNav.MyPage)

    BottomBar(screens = screens, currentDestination = currentDestination) {bottomNav ->

        val navOptions = navOptions {
            // Pop up to the start destination of the graph to
            // avoid building up a large stack of destinations
            // on the back stack as users select items
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
            }
            // Avoid multiple copies of the same destination when
            // reselecting the same item
            launchSingleTop = true

            // Restore state when reselecting a previously selected item
            restoreState = true
        }
        when (bottomNav) {
            MainBottomNav.MyPage -> navController.navigateToMyPage(navOptions)
            MainBottomNav.Order -> navController.navigateToOrder(navOptions)
            MainBottomNav.Profile -> navController.navigateToProfile(navOptions)
        }
    }
}


@Composable
fun BottomBar(
    screens: List<MainBottomNav>,
    currentDestination: NavDestination?,
    onBottomNavClick: (MainBottomNav) -> Unit
) {
    CafeNavigationBar {
        screens.forEach { destination ->
            val selected = currentDestination.isTopLevelDestinationInHierarchy(destination.route)
            CafeNavigationBarItem(
                selected = selected,
                onClick = { onBottomNavClick(destination) },
                icon = {
                    val icon = if (selected) {
                        destination.selectedIcon
                    } else {
                        destination.unselectedIcon
                    }
                    when (icon) {
                        is Icon.ImageVectorIcon -> Icon(
                            imageVector = icon.imageVector,
                            contentDescription = null,
                        )

                        is Icon.DrawableResourceIcon -> Icon(
                            painter = painterResource(id = icon.id),
                            contentDescription = null,
                        )
                    }
                },
                label = { Text(stringResource(destination.titleId)) },
            )
        }
    }

}

private fun NavDestination?.isTopLevelDestinationInHierarchy(route: String) =
    this?.hierarchy?.any { it.route?.contains(route, true) ?: false } ?: false