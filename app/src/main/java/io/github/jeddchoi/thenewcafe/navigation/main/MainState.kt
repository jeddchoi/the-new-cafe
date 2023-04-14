package io.github.jeddchoi.thenewcafe.navigation.main

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
import io.github.jeddchoi.profile.ProfileNavigation
import io.github.jeddchoi.profile.navigateToProfile
import io.github.jeddchoi.mypage.MyPageNavigation
import io.github.jeddchoi.mypage.navigateToMyPage
import io.github.jeddchoi.store_list.StoreListNavigation
import io.github.jeddchoi.store_list.navigateToOrder
import io.github.jeddchoi.ui.feature.BottomNavigation
import kotlinx.coroutines.CoroutineScope


@Composable
fun rememberMainState(
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
    navController: NavHostController = rememberNavController(),
): MainState {
    return remember(navController, coroutineScope) {
        MainState(navController, coroutineScope)
    }
}


@Stable
class MainState(
    val navController: NavHostController,
    private val coroutineScope: CoroutineScope,
) {

    val currentDestination: NavDestination?
        @Composable get() = navController.currentBackStackEntryAsState().value?.destination

    val currentTopLevelBottomNav: BottomNavigation?
        @Composable get() = when (currentDestination?.route) {
            ProfileNavigation.route() -> ProfileNavigation
            StoreListNavigation.route() -> StoreListNavigation
            MyPageNavigation.route() -> MyPageNavigation
            else -> null
        }

    /**
     * Map of top level destinations to be used in the TopBar, BottomBar and NavRail. The key is the
     * route.
     */
    val bottomNavigations: List<BottomNavigation> =
        listOf(ProfileNavigation, StoreListNavigation, MyPageNavigation)


    fun onNavigateToBottomNav(bottomNavigation: BottomNavigation) {
        val bottomNavOptions = navOptions {
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

        when (bottomNavigation) {
            is ProfileNavigation -> navController.navigateToProfile(bottomNavOptions)
            is StoreListNavigation -> navController.navigateToOrder(bottomNavOptions)
            is MyPageNavigation -> navController.navigateToMyPage(bottomNavOptions)
        }
    }

    // TODO: Add reselection of BottomNavigation
//    private var _reselectBottomNav = MutableStateFlow(false)
//
//    val reselectBottomNav : StateFlow<Boolean>
//        get() = _reselectBottomNav
//
//    fun reselectBottomNav() {
//        _reselectBottomNav.value = true
//    }
//
//    fun handleReselectBottomNav() {
//        _reselectBottomNav.value = false
//    }
//    fun onBottomNavReselection(lazyListState: LazyListState) {
//        coroutineScope.launch {
//            lazyListState.animateScrollToItem(0)
//        }
//    }
}

