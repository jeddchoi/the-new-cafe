package io.github.jeddchoi.historydetail

import android.content.Intent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
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


private const val HistoryDetailArg = "sessionId"
private const val HistoryDetailRoutePattern = "history?$HistoryDetailArg={$HistoryDetailArg}"

internal class HistoryDetailArgs(val sessionId: String) {
    constructor(savedStateHandle: SavedStateHandle) :
            this(checkNotNull(savedStateHandle[HistoryDetailArg]) as String)
}

fun NavController.navigateToHistoryDetail(
    sessionId: String,
    navOptions: NavOptions? = null,
) {
    navigate("history?$HistoryDetailArg=$sessionId", navOptions)
}


fun NavGraphBuilder.historyDetailScreen(
    clickBack: () -> Unit,
) {
    composable(
        route = HistoryDetailRoutePattern,
        arguments = listOf(
            navArgument(HistoryDetailArg) {
                type = NavType.StringType
                nullable = false
            }
        ),
        deepLinks = listOf(
            navDeepLink {
                uriPattern = "https://io.github.jeddchoi.thenewcafe/$HistoryDetailRoutePattern"
                action = Intent.ACTION_VIEW
            },
            navDeepLink {
                uriPattern = "jeddchoi://thenewcafe/$HistoryDetailRoutePattern"
                action = Intent.ACTION_VIEW
            }
        )
    ) { _ ->
        val viewModel: HistoryDetailViewModel = hiltViewModel()

        val uiState by viewModel.uiState.collectAsStateWithLifecycle()
        HistoryDetailScreen(
            clickBack = clickBack,
            modifier = Modifier.fillMaxSize(),
            uiState = uiState,
        )
    }
}