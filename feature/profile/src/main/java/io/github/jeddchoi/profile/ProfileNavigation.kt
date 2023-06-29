package io.github.jeddchoi.profile

import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import io.github.jeddchoi.ui.model.UiState

private const val ProfileRoutePattern = "profile"

/**
 * Navigate to profile
 *
 * @param navOptions
 */
fun NavController.navigateToProfile(navOptions: NavOptions? = null) {
    this.navigate(ProfileRoutePattern, navOptions)
}

/**
 * Bridge to Profile screen
 *
 * @param onNavigateToSignIn
 * @param onBackClick
 * @receiver
 * @receiver
 */
fun NavGraphBuilder.profileScreen(
    onNavigateToSignIn: () -> Unit,
    onBackClick: () -> Unit,
) {
    composable(
        route = ProfileRoutePattern,
    ) {
        val viewModel: ProfileViewModel = hiltViewModel()
        val uiState by viewModel.uiState.collectAsStateWithLifecycle(UiState.InitialLoading)

        ProfileScreen(
            uiState = uiState,
            onNavigateToSignIn = onNavigateToSignIn,
            onSignOut = {
                viewModel.signOut()
                onNavigateToSignIn()
            },
            onBackClick = onBackClick,
        )
    }
}