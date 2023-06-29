package io.github.jeddchoi.thenewcafe.navigation.root

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.navOptions
import io.github.jeddchoi.authentication.authGraph
import io.github.jeddchoi.authentication.navigateToAuth
import io.github.jeddchoi.thenewcafe.navigation.main.mainScreen
import io.github.jeddchoi.thenewcafe.navigation.main.navigateToMain

@Composable
fun RootNavGraph(
    navController: NavHostController,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    startDestination: RootNavScreen = RootNavScreen.Auth
) {
    NavHost(
        navController = navController,
        startDestination = startDestination.route,
        modifier = modifier,
    ) {

        authGraph(navController = navController, onBackClick = onBackClick, navigateToMain = {
            navController.navigateToMain(navOptions = navOptions {
                popUpTo(navController.graph.findStartDestination().id) {
                    inclusive = true
                }
                launchSingleTop = true
            })
        })

        mainScreen {
            navController.navigateToAuth(navOptions = navOptions {
                popUpTo(navController.graph.findStartDestination().id)
                launchSingleTop = true
            })
        }
    }
}



