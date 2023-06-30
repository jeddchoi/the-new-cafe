package io.github.jeddchoi.thenewcafe.navigation.main

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable

private const val MainRoutePattern = "main"

fun NavController.navigateToMain(navOptions: NavOptions? = null) {
    this.navigate(MainRoutePattern, navOptions)
}

@OptIn(ExperimentalAnimationApi::class)
fun NavGraphBuilder.mainScreen(
    mainNavController: NavHostController,
    navigateToSignIn: () -> Unit,
) {
    composable(
        route = MainRoutePattern,
    ) {
        val mainState = rememberMainState(navController = mainNavController)
        MainScreen(mainState = mainState, navigateToSignIn = navigateToSignIn)
    }
}
