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
import io.github.jeddchoi.mypage.myPageScreen
import io.github.jeddchoi.order.navigateToOrder
import io.github.jeddchoi.order.orderGraph
import io.github.jeddchoi.order.store.navigateToStore
import io.github.jeddchoi.order.store.storeScreen
import io.github.jeddchoi.order.store_list.storeListScreen
import io.github.jeddchoi.profile.profileScreen
import io.github.jeddchoi.thenewcafe.ui.main.MainRoutePattern
import io.github.jeddchoi.thenewcafe.ui.main.mainGraph
import io.github.jeddchoi.thenewcafe.ui.main.navigateToMain

@Composable
fun RootNavGraph(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    redirectToAuth: Boolean = false,
) {
    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = MainRoutePattern,
    ) {
        authGraph {
            val navigateToMain = {
                navController.navigateToMain(navOptions = navOptions {
                    popUpTo(navController.graph.findStartDestination().id)
                    launchSingleTop = true
                })
            }
            signInScreen(
                navController = navController,
                navigateToMain = navigateToMain,
            )
            registerScreen(
                navController = navController,
                navigateToMain = navigateToMain,
            )
        }

        mainGraph {
            profileScreen(
                navigateToAuth = navController::navigateToAuth,
            )
            orderGraph {
                storeListScreen(
                    navigateToStore = navController::navigateToStore
                )
                storeScreen(
                    onBackClick = navController::navigateUp,
                    navigateToAuth = navController::navigateToAuth
                )
            }
            myPageScreen(
                navigateToStoreList = navController::navigateToOrder,
                navigateToStore = navController::navigateToStore,
            )
        }
    }
    LaunchedEffect(redirectToAuth) {
        if (redirectToAuth) {
            navController.navigateToAuth()
        }
    }
}
