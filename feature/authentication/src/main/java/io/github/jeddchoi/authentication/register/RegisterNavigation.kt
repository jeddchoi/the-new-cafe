package io.github.jeddchoi.authentication.register

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import io.github.jeddchoi.authentication.AuthGraphRoutePattern
import io.github.jeddchoi.authentication.AuthViewModel

private const val RegisterRoutePattern = "register"

fun NavController.navigateToRegister(navOptions: NavOptions? = null) {
    this.navigate(RegisterRoutePattern, navOptions)
}


@OptIn(ExperimentalAnimationApi::class)
fun NavGraphBuilder.registerScreen(
    navController: NavHostController,
    navigateToMain: () -> Unit = {}
) {
    composable(
        route = RegisterRoutePattern,
    ) { backStackEntry ->
        val authEntry = remember(backStackEntry) {
            navController.getBackStackEntry(AuthGraphRoutePattern)
        }
        val authViewModel = hiltViewModel<AuthViewModel>(authEntry)
        val uiState by authViewModel.uiState.collectAsStateWithLifecycle()
        RegisterScreen(
            uiState = uiState,
            onBackClick = navController::popBackStack,
            navigateToSignInClick = navController::popBackStack,
            navigateToMain = navigateToMain,
            changeDisplayNameInput = authViewModel::checkDisplayNameInput,
            changeEmailInput = authViewModel::checkEmailInput,
            changePasswordInput = authViewModel::checkPasswordInput,
            changeConfirmPasswordInput = authViewModel::checkConfirmPasswordInput,
            register = authViewModel::register,
            dismissUserMessage = authViewModel::dismissUserMessage
        )
    }
}

