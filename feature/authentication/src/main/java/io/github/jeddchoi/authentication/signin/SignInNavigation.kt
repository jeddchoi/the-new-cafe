package io.github.jeddchoi.authentication.signin

import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navOptions
import io.github.jeddchoi.authentication.AuthGraphRoutePattern
import io.github.jeddchoi.authentication.AuthViewModel
import io.github.jeddchoi.authentication.register.navigateToRegister


internal const val SignInRoutePattern = "signin"

internal fun NavGraphBuilder.signInScreen(
    navController: NavHostController,
    onBackClick: () -> Unit,
    navigateToMain: () -> Unit
) {
    composable(
        route = SignInRoutePattern,
    ) { backStackEntry ->
        val authEntry = remember(backStackEntry) {
            navController.getBackStackEntry(AuthGraphRoutePattern)
        }
        val authViewModel = hiltViewModel<AuthViewModel>(authEntry)
        SignInScreen(
            viewModel = authViewModel,
            onBackClick = onBackClick,
            navigateToMain = navigateToMain,
            navigateToRegisterClick = {
                navController.navigateToRegister(navOptions {
                    popUpTo(AuthGraphRoutePattern)
                    launchSingleTop = true
                })
            },

            )
    }
}