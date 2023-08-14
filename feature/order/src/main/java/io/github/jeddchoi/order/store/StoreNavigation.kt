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

private const val STORE_ID_ARG = "storeId"
private const val SECTION_ID_ARG = "sectionId"
private const val SEAT_ID_ARG = "seatId"
private const val STORE_ROUTE_PATTERN =
    "stores?$STORE_ID_ARG={$STORE_ID_ARG}&$SECTION_ID_ARG={$SECTION_ID_ARG}&$SEAT_ID_ARG={$SEAT_ID_ARG}"

internal class StoreArgs(val storeId: String, val sectionId: String?, val seatId: String?) {
    constructor(savedStateHandle: SavedStateHandle) :
            this(
                checkNotNull(savedStateHandle[STORE_ID_ARG]) as String,
                savedStateHandle[SECTION_ID_ARG],
                savedStateHandle[SEAT_ID_ARG]
            )
}

fun NavController.navigateToStore(
    storeId: String,
    sectionId: String? = null,
    seatId: String? = null,
    navOptions: NavOptions? = null,
) {
    navigate("stores?$STORE_ID_ARG=$storeId&$SECTION_ID_ARG=$sectionId&$SEAT_ID_ARG=$seatId", navOptions)
}


fun NavGraphBuilder.storeScreen(
    onBackClick: () -> Unit,
    navigateToAuth: () -> Unit,
    navigateToMyPage: () -> Unit,
) {
    composable(
        route = STORE_ROUTE_PATTERN,
        arguments = listOf(
            navArgument(STORE_ID_ARG) {
                type = NavType.StringType
                nullable = false
            },
            navArgument(SECTION_ID_ARG) {
                type = NavType.StringType
                nullable = true
            },
            navArgument(SEAT_ID_ARG) {
                type = NavType.StringType
                nullable = true
            }
        ),
        deepLinks = listOf(
            navDeepLink {
                uriPattern = "https://io.github.jeddchoi.thenewcafe/$STORE_ROUTE_PATTERN"
                action = Intent.ACTION_VIEW
            },
            navDeepLink {
                uriPattern = "jeddchoi://thenewcafe/$STORE_ROUTE_PATTERN"
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