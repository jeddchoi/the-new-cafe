package io.github.jeddchoi.order.store_list

import android.content.Intent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.navDeepLink
import io.github.jeddchoi.ui.feature.ScreenWithBottomBar


internal const val StoreListRoutePattern = "stores"

fun NavController.navigateToStoreList(
    navOptions: NavOptions? = null
) {
    navigate(StoreListRoutePattern, navOptions)
}

@OptIn(ExperimentalAnimationApi::class)
fun NavGraphBuilder.storeListScreen(
    navController: NavController,
    bottomBar : @Composable () -> Unit,
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

        ScreenWithBottomBar(
            bottomBar
        ) {
            StoreListScreen()
        }
    }
}