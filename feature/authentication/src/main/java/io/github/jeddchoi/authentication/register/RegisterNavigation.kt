package io.github.jeddchoi.authentication.register

import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.navOptions
import io.github.jeddchoi.authentication.AuthGraphRoutePattern
import io.github.jeddchoi.authentication.AuthViewModel
import io.github.jeddchoi.authentication.navigateToAuth

private const val RegisterRoutePattern = "register"

fun NavController.navigateToRegister(navOptions: NavOptions? = null) {
    this.navigate(RegisterRoutePattern, navOptions)
}


internal fun NavGraphBuilder.registerScreen(
    navController: NavHostController,
    onBackClick: () -> Unit,
    navigateToMain: () -> Unit
) {
    composable(
        route = RegisterRoutePattern,
    ) { backStackEntry ->
        val authEntry = remember(backStackEntry) {
            navController.getBackStackEntry(AuthGraphRoutePattern)
        }
        val authViewModel = hiltViewModel<AuthViewModel>(authEntry)
        RegisterScreen(
            viewModel = authViewModel,
            onBackClick = onBackClick,
            navigateToSignInClick = {
                navController.navigateToAuth(navOptions {
                    popUpTo(AuthGraphRoutePattern) {
                        inclusive = true
                    }
                    launchSingleTop = true
                })
            },
            navigateToMain = navigateToMain,
        )
    }
}

