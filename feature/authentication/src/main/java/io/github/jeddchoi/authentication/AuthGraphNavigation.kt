package io.github.jeddchoi.authentication

import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavOptions
import androidx.navigation.navigation
import io.github.jeddchoi.authentication.register.registerScreen
import io.github.jeddchoi.authentication.signin.SignInRoutePattern
import io.github.jeddchoi.authentication.signin.signInScreen


internal const val AuthGraphRoutePattern = "auth"

fun NavController.navigateToAuth(navOptions: NavOptions? = null) {
    this.navigate(AuthGraphRoutePattern, navOptions)
}

fun NavGraphBuilder.authGraph(
    navController: NavHostController,
    onBackClick: () -> Unit,
    navigateToMain: () -> Unit,
    modifier: Modifier = Modifier,
) {
    navigation(
        route = AuthGraphRoutePattern,
        startDestination = SignInRoutePattern,
    ) {
        signInScreen(navController, onBackClick, navigateToMain)
        registerScreen(navController, onBackClick, navigateToMain)
    }
}

