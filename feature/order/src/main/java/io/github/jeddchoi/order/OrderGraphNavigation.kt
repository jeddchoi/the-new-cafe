package io.github.jeddchoi.order

import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.navOptions
import androidx.navigation.navigation
import io.github.jeddchoi.order.store.storeScreen
import io.github.jeddchoi.order.store_list.StoreListRoutePattern
import io.github.jeddchoi.order.store_list.storeListScreen

private const val OrderGraphRoutePattern = "order"

fun NavController.navigateToOrder(
    navOptions: NavOptions = navOptions {
        popUpTo(graph.findStartDestination().id) {
            saveState = true
        }
        launchSingleTop = true
        restoreState = true
    }
) {
    navigate(OrderGraphRoutePattern, navOptions)
}

fun NavGraphBuilder.orderGraph(
    navController: NavController,
) {
    navigation(
        route = OrderGraphRoutePattern,
        startDestination = StoreListRoutePattern
    ) {
        storeListScreen()
        storeScreen()
    }
}