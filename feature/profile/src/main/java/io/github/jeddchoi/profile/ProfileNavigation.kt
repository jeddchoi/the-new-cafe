package io.github.jeddchoi.profile

import android.content.Intent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.navDeepLink
import androidx.navigation.navigation
import io.github.jeddchoi.ui.LogCompositions

private const val ProfileGraphRoutePattern = "profile_graph"
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
 * @param navigateToAuth
 * @param onBackClick
 * @receiver
 */



fun NavGraphBuilder.profileGraph(
    nestedGraphs: NavGraphBuilder.() -> Unit,
) {
    navigation(
        route = ProfileGraphRoutePattern,
        startDestination = ProfileRoutePattern
    ) {
        nestedGraphs()
    }
}

@OptIn(ExperimentalAnimationApi::class)
fun NavGraphBuilder.profileScreen(
    navigateToAuth: () -> Unit,
) {
    composable(
        route = ProfileRoutePattern,
        deepLinks = listOf(
            navDeepLink {
                uriPattern = "https://io.github.jeddchoi.thenewcafe/$ProfileRoutePattern"
                action = Intent.ACTION_VIEW
            },
            navDeepLink {
                uriPattern = "jeddchoi://thenewcafe/$ProfileRoutePattern"
                action = Intent.ACTION_VIEW
            }
        )
    ) {
        val viewModel: ProfileViewModel = hiltViewModel()
        val uiState by viewModel.uiState.collectAsStateWithLifecycle()
        LogCompositions(tag = "Profile", msg = "ProfileScreen")
        ProfileScreen(
            uiState = uiState,
            navigateToSignIn = navigateToAuth,
            modifier = Modifier.fillMaxSize(),
            signOut = viewModel::signOut
        )
    }
}