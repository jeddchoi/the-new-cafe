package io.github.jeddchoi.order.store

import android.content.Intent
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink

private const val StoreIdArg = "storeId"
private const val StoreRoutePattern = "stores?$StoreIdArg={$StoreIdArg}"

internal class StoreArgs(val storeId: String) {
    constructor(savedStateHandle: SavedStateHandle) :
            this(checkNotNull(savedStateHandle[StoreIdArg]) as String)
}

fun NavController.navigateToStore(
    storeId: String,
    navOptions: NavOptions? = null,
) {
    navigate("stores?$StoreIdArg=$storeId", navOptions)
}


fun NavGraphBuilder.storeScreen(
    onBackClick: () -> Unit,
    navigateToAuth: () -> Unit,
    navigateToMyPage: () -> Unit,
) {
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
            },
        )
    ) {
        val viewModel: StoreViewModel = hiltViewModel()
        val uiState by viewModel.uiState.collectAsStateWithLifecycle()
        StoreScreen(
            uiState = uiState,
            onSelect = viewModel::selectSeat,
            onBackClick = onBackClick,
            reserve = {
                viewModel.reserve()
                navigateToMyPage()
            },
            quit = viewModel::quit,
            changeSeat = {
                viewModel.quitAndReserve()
                navigateToMyPage()
            },
            navigateToSignIn = navigateToAuth,
            setUserMessage = viewModel::setUserMessage
        )
    }
}