package io.github.jeddchoi.order

import android.util.Log
import androidx.navigation.*
import androidx.navigation.compose.composable
import io.github.jeddchoi.ui.LogCompositions


private const val orderGraphRoutePattern = "order_graph"
const val orderRoute = "order"


fun NavController.navigateToOrderGraph(navOptions: NavOptions? = null) {
    Log.i("TAG", "Navigate to Order")
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
                navDeepLink { uriPattern = "$baseWebUri/$orderRoute" },
                navDeepLink { uriPattern = "$baseAppUri/$orderRoute" }
            )
        ) {backStackEntry ->
            LogCompositions(tag = "TAG", msg = "Order : backStackEntry = ${backStackEntry.arguments}")
            OrderRoute(
                navigateToSeats = navigateToStore,
            )
        }
        nestedGraphs()
    }
}
