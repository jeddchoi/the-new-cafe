package io.github.jeddchoi.order

import androidx.compose.runtime.Composable
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
    bottomBar : @Composable () -> Unit,
) {
    navigation(
        route = OrderGraphRoutePattern,
        startDestination = StoreListRoutePattern
    ) {
        storeListScreen(
            navController = navController,
            bottomBar = bottomBar
        )
        storeScreen()
    }
}