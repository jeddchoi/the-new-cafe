package io.github.jeddchoi.order.store_list

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.navOptions


internal const val StoreListRoutePattern = "stores"

fun NavController.navigateToStoreList(
    navOptions: NavOptions = navOptions {
        popUpTo(graph.findStartDestination().id) {
            saveState = true
        }
        launchSingleTop = true
        restoreState = true
    }
) {
    navigate(StoreListRoutePattern, navOptions)
}

fun NavGraphBuilder.storeListScreen() {
    composable(
        route = StoreListRoutePattern,
    ) {
        val viewModel: StoreListViewModel = hiltViewModel()
        StoreListScreen()
    }
}