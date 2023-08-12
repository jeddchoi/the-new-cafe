package io.github.jeddchoi.order.store_list

import android.content.Intent
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
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

fun NavGraphBuilder.storeListScreen(
    navigateToStore: (String) -> Unit,
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
        val uiState by viewModel.uiState.collectAsStateWithLifecycle()
        StoreListScreen(
            uiState = uiState,
            navigateToStore = navigateToStore
        )
    }
}