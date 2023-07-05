package io.github.jeddchoi.authentication.signin

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import io.github.jeddchoi.authentication.AuthGraphRoutePattern
import io.github.jeddchoi.authentication.AuthViewModel
import io.github.jeddchoi.authentication.register.navigateToRegister


internal const val SignInRoutePattern = "signin"

@OptIn(ExperimentalAnimationApi::class)
fun NavGraphBuilder.signInScreen(
    navController: NavHostController,
    navigateToMain: () -> Unit
) {
    composable(
        route = SignInRoutePattern,
    ) { backStackEntry ->
        val authEntry = remember(backStackEntry) {
            navController.getBackStackEntry(AuthGraphRoutePattern)
        }
        val authViewModel : AuthViewModel = hiltViewModel(authEntry)
        SignInScreen(
            viewModel = authViewModel,
            onBackClick = navController::popBackStack,
            navigateToMain = navigateToMain,
            navigateToRegister = navController::navigateToRegister,
        )
    }
}