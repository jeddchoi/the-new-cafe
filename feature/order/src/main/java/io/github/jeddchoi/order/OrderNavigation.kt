package io.github.jeddchoi.order

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.navigation


private const val orderGraphRoutePattern = "order_graph"
const val orderRoute = "order_route"


fun NavController.navigateToOrderGraph(navOptions: NavOptions? = null) {
    this.navigate(orderGraphRoutePattern, navOptions)
}

fun NavGraphBuilder.orderGraph(
    navigateToStore: (String) -> Unit,
    nestedGraphs: NavGraphBuilder.() -> Unit,
) {
    navigation(
        route = orderGraphRoutePattern,
        startDestination = orderRoute,
    ) {
        composable(route = orderRoute) {
            OrderRoute(
                navigateToSeats = navigateToStore,
            )
        }
        nestedGraphs()
    }
}
