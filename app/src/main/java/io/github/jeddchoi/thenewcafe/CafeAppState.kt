package io.github.jeddchoi.thenewcafe

import android.util.Log
import androidx.compose.runtime.*
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import io.github.jeddchoi.account.AccountNavigation
import io.github.jeddchoi.mypage.MyPageNavigation
import io.github.jeddchoi.mypage.MyPageTab
import io.github.jeddchoi.store.StoreNavigation
import io.github.jeddchoi.store_list.StoreListNavigation
import io.github.jeddchoi.ui.feature.AppNavigation
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

    val currentAppDestination: AppNavigation?
        @Composable get() {
            return when (currentDestination?.route) {
                AccountNavigation.route() -> AccountNavigation
                StoreListNavigation.route() -> StoreListNavigation
                MyPageNavigation.route() -> MyPageNavigation
                StoreNavigation.route() -> StoreNavigation
                else -> null
            }
        }

    /**
     * Map of top level destinations to be used in the TopBar, BottomBar and NavRail. The key is the
     * route.
     */
    val topLevelDestinations: List<AppNavigation> =
        listOf(AccountNavigation, StoreListNavigation, MyPageNavigation)


    fun navigateToActionLog() {
        navController.navigateToSingleTopDestination(MyPageNavigation, MyPageTab.ACTION_LOG.name)
        Log.e("TAG", " THIS : ${navController.currentBackStackEntry?.arguments?.getString(MyPageNavigation.tabIdArg)}")
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


/**
 * UI logic for navigating to a top level destination in the app. Top level destinations have
 * only one copy of the destination of the back stack, and save and restore state whenever you
 * navigate to and from it.
 *
 * @param appDestination: The destination the app needs to navigate to.
 */
fun NavController.navigateToSingleTopDestination(
    appDestination: AppNavigation,
    arg: String? = null
) = this.navigate(route = appDestination.route(arg)) {
    // Pop up to the start destination of the graph to
    // avoid building up a large stack of destinations
    // on the back stack as users select items
    popUpTo(
        this@navigateToSingleTopDestination.graph.findStartDestination().id
    ) {
        saveState = true

    }
    // Avoid multiple copies of the same destination when
    // reselecting the same item
    launchSingleTop = true
    // Restore state when reselecting a previously selected item
    restoreState = true
}


//        when (appDestination) {
//            AccountNavigation -> navController.navigateToAccount(topLevelNavOptions)
//            StoreListNavigation -> navController.navigateToOrderGraph(topLevelNavOptions)
//            MyPageNavigation -> navController.navigateToMyPage(topLevelNavOptions)
//        }

