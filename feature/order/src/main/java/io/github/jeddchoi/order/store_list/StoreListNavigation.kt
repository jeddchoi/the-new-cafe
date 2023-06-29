package io.github.jeddchoi.order.store_list

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.navDeepLink


internal const val StoreListRoutePattern = "stores"

fun NavController.navigateToStoreList(
    navOptions: NavOptions? = null
) {
    navigate(StoreListRoutePattern, navOptions)
}

@OptIn(ExperimentalAnimationApi::class)
fun NavGraphBuilder.storeListScreen() {
    composable(
        route = StoreListRoutePattern,
        deepLinks = listOf(
            navDeepLink {
                uriPattern = "https://io.github.jeddchoi.thenewcafe/$StoreListRoutePattern"
            },
            navDeepLink {
                uriPattern = "jeddchoi://thenewcafe/$StoreListRoutePattern"
            }
        )
    ) {
        val viewModel: StoreListViewModel = hiltViewModel()
        StoreListScreen()
    }
}