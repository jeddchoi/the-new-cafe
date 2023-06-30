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
    rootNavController: NavHostController,
    mainNavController: NavHostController,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    startDestination: RootNavScreen = RootNavScreen.Auth
) {
    NavHost(
        navController = rootNavController,
        startDestination = startDestination.route,
        modifier = modifier,
    ) {

        authGraph(navController = rootNavController, onBackClick = onBackClick, navigateToMain = {
            rootNavController.navigateToMain(navOptions = navOptions {
                popUpTo(rootNavController.graph.findStartDestination().id) {
                    inclusive = true
                }
                launchSingleTop = true
            })
        })

        mainScreen(
            mainNavController = mainNavController
        ) {
            rootNavController.navigateToAuth(navOptions = navOptions {
                popUpTo(rootNavController.graph.findStartDestination().id)
                launchSingleTop = true
            })
        }
    }
}



