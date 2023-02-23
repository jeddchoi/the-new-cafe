package io.github.jeddchoi.thenewcafe

import androidx.compose.runtime.*
import androidx.navigation.NavDestination
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navOptions
import io.github.jeddchoi.account.AccountNavigation
import io.github.jeddchoi.account.navigateToAccount
import io.github.jeddchoi.mypage.MyPageNavigation
import io.github.jeddchoi.mypage.MyPageTab
import io.github.jeddchoi.mypage.navigateToMyPage
import io.github.jeddchoi.order.OrderNavigation
import io.github.jeddchoi.order.navigateToOrderGraph
import io.github.jeddchoi.thenewcafe.navigation.TopLevelDestination
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
        @Composable get() {
            return when (currentDestination?.route) {
                AccountNavigation.route() -> TopLevelDestination.ACCOUNT
                OrderNavigation.route() -> TopLevelDestination.ORDER
                MyPageNavigation.route() -> TopLevelDestination.MYPAGE
                else -> null
            }
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
    fun navigateToTopLevelDestination(
        topLevelDestination: TopLevelDestination,
    ) {
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
            TopLevelDestination.ORDER -> navController.navigateToOrderGraph(topLevelNavOptions)
            TopLevelDestination.MYPAGE -> navController.navigateToMyPage(topLevelNavOptions)
        }
    }

    fun navigateToActionLog() {
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

        navController.navigateToMyPage(topLevelNavOptions, MyPageTab.ACTION_LOG)
    }



    var shouldHandleReselection by mutableStateOf(false)
        private set


    fun onBackClick() {
        navController.popBackStack()
    }

    fun setHandleReselection(shouldHandle: Boolean) {
        shouldHandleReselection = shouldHandle
    }



}
