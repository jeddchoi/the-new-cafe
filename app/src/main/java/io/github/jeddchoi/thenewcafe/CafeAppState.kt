package io.github.jeddchoi.thenewcafe

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.navigation.NavDestination
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navOptions
import io.github.jeddchoi.account.accountRoute
import io.github.jeddchoi.account.navigateToAccount
import io.github.jeddchoi.mypage.myPageRoute
import io.github.jeddchoi.mypage.navigateToMyPage
import io.github.jeddchoi.stores.navigateToStoresGraph
import io.github.jeddchoi.stores.storesRoute
import kotlinx.coroutines.CoroutineScope


@Composable
fun rememberCafeAppState(
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
    navController: NavHostController = rememberNavController(),
): CafeAppState {
    return remember(navController, coroutineScope) {
        CafeAppState(navController, coroutineScope)
    }
}

@Stable
class CafeAppState(
    val navController: NavHostController,
    val coroutineScope: CoroutineScope,
) {
    val currentDestination: NavDestination?
        @Composable get() = navController
            .currentBackStackEntryAsState().value?.destination

    val currentTopLevelDestination: TopLevelDestination?
        @Composable get() = when (currentDestination?.route) {
            accountRoute -> TopLevelDestination.ACCOUNT
            storesRoute -> TopLevelDestination.ORDER
            myPageRoute -> TopLevelDestination.MYPAGE
            else -> null
        }

    /**
     * Map of top level destinations to be used in the TopBar, BottomBar and NavRail. The key is the
     * route.
     */
    val topLevelDestinations: List<TopLevelDestination> = TopLevelDestination.values().asList()

    /**
     * UI logic for navigating to a top level destination in the app. Top level destinations have
     * only one copy of the destination of the back stack, and save and restore state whenever you
     * navigate to and from it.
     *
     * @param topLevelDestination: The destination the app needs to navigate to.
     */
    fun navigateToTopLevelDestination(topLevelDestination: TopLevelDestination) {
        val topLevelNavOptions = navOptions {
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

        when (topLevelDestination) {
            TopLevelDestination.ACCOUNT -> navController.navigateToAccount(topLevelNavOptions)
            TopLevelDestination.ORDER -> navController.navigateToStoresGraph(topLevelNavOptions)
            TopLevelDestination.MYPAGE -> navController.navigateToMyPage(topLevelNavOptions)
        }

    }

    fun onBackClick() {
        navController.popBackStack()
    }
}
