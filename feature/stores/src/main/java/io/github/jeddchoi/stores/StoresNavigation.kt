package io.github.jeddchoi.stores

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation


private const val storesGraphRoutePattern = "stores_graph"
const val storesRoute = "stores_route"


fun NavGraphBuilder.storesGraph(
    navigateToSeats: (String) -> Unit,
    nestedGraphs: NavGraphBuilder.() -> Unit,
) {
    navigation(
        route = storesGraphRoutePattern,
        startDestination = storesRoute,
    ) {
        composable(route = storesRoute) {
            StoresRoute(
                navigateToSeats = navigateToSeats,
            )
        }
        nestedGraphs()
    }
}
