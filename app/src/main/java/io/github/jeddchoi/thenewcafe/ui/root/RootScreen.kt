package io.github.jeddchoi.thenewcafe.ui.root

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navOptions
import io.github.jeddchoi.authentication.authGraph
import io.github.jeddchoi.authentication.navigateToAuth
import io.github.jeddchoi.authentication.register.registerScreen
import io.github.jeddchoi.authentication.signin.signInScreen
import io.github.jeddchoi.data.util.NetworkMonitor
import io.github.jeddchoi.historydetail.historyDetailScreen
import io.github.jeddchoi.historydetail.navigateToHistoryDetail
import io.github.jeddchoi.thenewcafe.ui.main.MainRoutePattern
import io.github.jeddchoi.thenewcafe.ui.main.mainScreen
import io.github.jeddchoi.thenewcafe.ui.main.navigateToMain

/**
 * Single entry point of composable world
 *
 * @param rootState : state holder of app
 */
@Composable
fun RootScreen(
    redirectToAuth: Boolean,
    networkMonitor: NetworkMonitor,
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    rootState: RootState = rememberRootState(
        navController = navController
    ),
) {
    NavHost(
        modifier = modifier,
        navController = rootState.navController,
        startDestination = MainRoutePattern,

    ) {
        authGraph {
            val navigateToMain = {
                rootState.navController.navigateToMain(navOptions = navOptions {
                    popUpTo(rootState.navController.graph.findStartDestination().id)
                    launchSingleTop = true
                })
            }
            signInScreen(
                navController = rootState.navController,
                navigateToMain = navigateToMain,
            )
            registerScreen(
                navController = rootState.navController,
                navigateToMain = navigateToMain,
            )
        }

        mainScreen(
            networkMonitor = networkMonitor,
            navigateToAuth = {
                rootState.navController.navigateToAuth(navOptions = navOptions {
                    popUpTo(rootState.navController.graph.findStartDestination().id)
                    launchSingleTop = true
                })
            },
            navigateToHistoryDetail = {
                rootState.navController.navigateToHistoryDetail(it)
            }
        )

        historyDetailScreen(
            clickBack = rootState.navController::popBackStack
        )
    }

    LaunchedEffect(redirectToAuth) {
        if (redirectToAuth) {
            rootState.navController.navigateToAuth()
        }
    }
}

