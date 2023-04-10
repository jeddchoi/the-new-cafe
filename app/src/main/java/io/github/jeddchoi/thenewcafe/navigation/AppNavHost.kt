package io.github.jeddchoi.thenewcafe.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import io.github.jeddchoi.thenewcafe.home.HomeNavigation
import io.github.jeddchoi.thenewcafe.home.homeScreen

@Composable
fun AppNavHost(
    navController: NavHostController,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    needToRedirectSignIn: Boolean = false,
) {
    NavHost(
        navController = navController,
        startDestination = if (needToRedirectSignIn) "AuthNavigation.name" else HomeNavigation.name, // TODO: fix this
        modifier = modifier
    ) {

        // TODO: add auth screen

        homeScreen(onBackClick)
    }
}



