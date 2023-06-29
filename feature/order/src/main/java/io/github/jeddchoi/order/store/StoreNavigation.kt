package io.github.jeddchoi.order.store

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navOptions

private const val StoreIdArg = "storeId"
private const val StoreRoutePattern = "stores/{$StoreIdArg}"

internal class StoreArgs(val storeId: String) {
    constructor(savedStateHandle: SavedStateHandle) :
            this(checkNotNull(savedStateHandle[StoreIdArg]) as String)
}

fun NavController.navigateToStore(
    storeId: String,
    navOptions: NavOptions = navOptions {
        popUpTo(graph.findStartDestination().id) {
            saveState = true
        }
        launchSingleTop = true
        restoreState = true
    },
) {
    navigate("stores/$storeId", navOptions)
}


fun NavGraphBuilder.storeScreen(

) {
    composable(
        route = StoreRoutePattern,
        arguments = listOf(
            navArgument(StoreIdArg) {
                type = NavType.StringType
                nullable = false
            }
        )
    ) {
        val viewModel: StoreViewModel = hiltViewModel()
        StoreScreen()
    }
}