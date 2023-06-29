package io.github.jeddchoi.thenewcafe.navigation.main

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable

private const val MainRoutePattern = "main"

fun NavController.navigateToMain(navOptions: NavOptions? = null) {
    this.navigate(MainRoutePattern, navOptions)
}

fun NavGraphBuilder.mainScreen(navigateToSignIn: () -> Unit) {
    composable(
        route = MainRoutePattern,
    ) {
        val mainState = rememberMainState()
        MainScreen(mainState = mainState, navigateToSignIn = navigateToSignIn)
    }
}
