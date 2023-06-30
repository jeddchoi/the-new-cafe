package io.github.jeddchoi.order

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.navigation
import io.github.jeddchoi.order.store.storeScreen
import io.github.jeddchoi.order.store_list.StoreListRoutePattern
import io.github.jeddchoi.order.store_list.storeListScreen

private const val OrderGraphRoutePattern = "order"

fun NavController.navigateToOrder(
    navOptions: NavOptions? = null
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
        storeListScreen(
            navController = navController,
        )
        storeScreen()
    }
}