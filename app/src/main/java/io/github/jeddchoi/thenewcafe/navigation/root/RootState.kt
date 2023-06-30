package io.github.jeddchoi.thenewcafe.navigation.root

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.map


@Composable
fun rememberRootState(
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
    navController: NavHostController = rememberNavController(),
): RootState {
    return remember(navController, coroutineScope) {
        RootState(navController, coroutineScope)
    }
}

@Stable
class RootState(
    val navController: NavHostController,
    val coroutineScope: CoroutineScope,
) {
    val currentDestination: NavDestination?
        @Composable get() = navController.currentBackStackEntryAsState().value?.destination

    val showNavigation = navController.currentBackStackEntryFlow.map { backStackEntry ->
        backStackEntry.destination.hierarchy.any {
            it.route?.contains(RootNavScreen.Main.route, true) ?: false
        }
    }

    fun onBackClick() {
        navController.popBackStack()
    }
}


