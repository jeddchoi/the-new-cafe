package io.github.jeddchoi.stores

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.navigation


private const val storesGraphRoutePattern = "stores_graph"
const val storesRoute = "stores_route"


fun NavController.navigateToStoresGraph(navOptions: NavOptions? = null) {
    this.navigate(storesGraphRoutePattern, navOptions)
}

fun NavGraphBuilder.storesGraph(
    navigateToStore: (String) -> Unit,
    nestedGraphs: NavGraphBuilder.() -> Unit,
) {
    navigation(
        route = storesGraphRoutePattern,
        startDestination = storesRoute,
    ) {
        composable(route = storesRoute) {
            StoresRoute(
                navigateToSeats = navigateToStore,
            )
        }
        nestedGraphs()
    }
}
