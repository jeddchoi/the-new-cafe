package io.github.jeddchoi.order

import androidx.navigation.*
import androidx.navigation.compose.composable


private const val orderGraphRoutePattern = "order_graph"
const val orderRoute = "order_route"


fun NavController.navigateToOrderGraph(navOptions: NavOptions? = null) {
    this.navigate(orderGraphRoutePattern, navOptions)
}

fun NavGraphBuilder.orderGraph(
    baseWebUri: String,
    baseAppUri: String,
    navigateToStore: (String) -> Unit,
    nestedGraphs: NavGraphBuilder.() -> Unit,
) {
    navigation(
        route = orderGraphRoutePattern,
        startDestination = orderRoute,
    ) {
        composable(
            route = orderRoute,
            deepLinks = listOf(
                navDeepLink { uriPattern = "$baseWebUri/ORDER" },
                navDeepLink { uriPattern = "$baseAppUri/ORDER" }
            )
        ) {
            OrderRoute(
                navigateToSeats = navigateToStore,
            )
        }
        nestedGraphs()
    }
}
