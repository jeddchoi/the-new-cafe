package io.github.jeddchoi.thenewcafe.navigation.root

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import io.github.jeddchoi.authentication.SigninNavigation
import io.github.jeddchoi.thenewcafe.navigation.PlaceholderScreen
import io.github.jeddchoi.thenewcafe.navigation.main.MainNavigation
import io.github.jeddchoi.thenewcafe.navigation.main.mainScreen

@Composable
fun RootNavGraph(
    navController: NavHostController,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    needToRedirectSignIn: Boolean = false,
) {
    NavHost(
        navController = navController,
        startDestination = if (needToRedirectSignIn) SigninNavigation.name else MainNavigation.name,
        modifier = modifier,

        ) {

        // TODO: change placeholder
        navigation(
            route = SigninNavigation.routeGraph,
            startDestination = SigninNavigation.route(),
        ) {
            composable(
                route = SigninNavigation.route(),
                deepLinks = SigninNavigation.deepLinks
            ) {
                PlaceholderScreen(title = "Sign In")
            }
        }

        mainScreen(onBackClick)
    }
}



