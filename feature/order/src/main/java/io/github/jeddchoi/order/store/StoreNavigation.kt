package io.github.jeddchoi.order.store

import android.content.Intent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink

private const val StoreIdArg = "storeId"
private const val StoreRoutePattern = "stores/{$StoreIdArg}"

internal class StoreArgs(val storeId: String) {
    constructor(savedStateHandle: SavedStateHandle) :
            this(checkNotNull(savedStateHandle[StoreIdArg]) as String)
}

fun NavController.navigateToStore(
    storeId: String,
    navOptions: NavOptions? = null,
) {
    navigate("stores/$storeId", navOptions)
}


@OptIn(ExperimentalAnimationApi::class)
fun NavGraphBuilder.storeScreen() {
    composable(
        route = StoreRoutePattern,
        arguments = listOf(
            navArgument(StoreIdArg) {
                type = NavType.StringType
                nullable = false
            }
        ),
        deepLinks = listOf(
            navDeepLink {
                uriPattern = "https://io.github.jeddchoi.thenewcafe/$StoreRoutePattern"
                action = Intent.ACTION_VIEW
            },
            navDeepLink {
                uriPattern = "jeddchoi://thenewcafe/$StoreRoutePattern"
                action = Intent.ACTION_VIEW
            }
        )
    ) {
        val viewModel: StoreViewModel = hiltViewModel()
        StoreScreen()
    }
}