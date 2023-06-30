package io.github.jeddchoi.order.store_list

import android.content.Intent
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
fun NavGraphBuilder.storeListScreen(
    navController: NavController,
) {
    composable(
        route = StoreListRoutePattern,
        deepLinks = listOf(
            navDeepLink {
                uriPattern = "https://io.github.jeddchoi.thenewcafe/$StoreListRoutePattern"
                action = Intent.ACTION_VIEW
            },
            navDeepLink {
                uriPattern = "jeddchoi://thenewcafe/$StoreListRoutePattern"
                action = Intent.ACTION_VIEW
            }
        )
    ) {
        val viewModel: StoreListViewModel = hiltViewModel()
        StoreListScreen()
    }
}