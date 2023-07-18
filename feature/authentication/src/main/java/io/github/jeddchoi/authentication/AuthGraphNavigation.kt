package io.github.jeddchoi.authentication

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.navigation
import io.github.jeddchoi.authentication.signin.SignInRoutePattern


internal const val AuthGraphRoutePattern = "auth"

fun NavController.navigateToAuth(navOptions: NavOptions? = null) {
    this.navigate(AuthGraphRoutePattern, navOptions)
}

fun NavGraphBuilder.authGraph(
    nestedGraphs: NavGraphBuilder.() -> Unit,
) {
    navigation(
        route = AuthGraphRoutePattern,
        startDestination = SignInRoutePattern,
    ) {
        nestedGraphs()
    }
}

