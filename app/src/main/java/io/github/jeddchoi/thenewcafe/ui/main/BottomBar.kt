package io.github.jeddchoi.thenewcafe.ui.main

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navOptions
import io.github.jeddchoi.designsystem.CafeNavigationBar
import io.github.jeddchoi.designsystem.CafeNavigationBarItem
import io.github.jeddchoi.mypage.navigateToMyPage
import io.github.jeddchoi.order.navigateToOrder
import io.github.jeddchoi.profile.navigateToProfile
import timber.log.Timber

@Composable
fun BottomBar(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    currentDestination: NavDestination? = null,
) {
    CafeNavigationBar(
        modifier = modifier
    ) {
        MainBottomNav.VALUES.forEach { destination ->
            val selected = currentDestination.isTopLevelDestinationInHierarchy(destination.route)
            CafeNavigationBarItem(
                selected = selected,
                onClick = {
                    Timber.v("✅")

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
                    when (destination) {
                        MainBottomNav.MyPage -> navController.navigateToMyPage(navOptions)
                        MainBottomNav.Order -> navController.navigateToOrder(navOptions)
                        MainBottomNav.Profile -> navController.navigateToProfile(navOptions)
                    }
                },
                selectedIcon = {
                    destination.selectedIcon.ToComposable()
                },
                unselectedIcon = {
                    destination.unselectedIcon.ToComposable()
                },
                label = { Text(stringResource(destination.titleId)) },
            )
        }
    }
}


private fun NavDestination?.isTopLevelDestinationInHierarchy(route: String): Boolean {
    Timber.v("✅ $route")
    return this?.hierarchy?.any { it.route?.contains(route, true) ?: false } ?: false
}