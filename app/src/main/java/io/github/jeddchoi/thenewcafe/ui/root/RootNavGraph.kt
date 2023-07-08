package io.github.jeddchoi.thenewcafe.ui.root

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
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
import io.github.jeddchoi.thenewcafe.ui.main.mainGraph
import io.github.jeddchoi.thenewcafe.ui.main.navigateToMain

@Composable
fun RootNavGraph(
    navController: NavHostController,
    shouldRedirectToAuth: Boolean,
    modifier: Modifier = Modifier,
) {
    NavHost(
        navController = navController,
        startDestination = RootNav.Main.route,
        modifier = modifier,
    ) {
        authGraph {
            val navigateToMain = {
                navController.navigateToMain(navOptions = navOptions {
                    popUpTo(navController.graph.findStartDestination().id)
                    launchSingleTop = true
                })
            }
            signInScreen(navController, navigateToMain)
            registerScreen(navController, navigateToMain)
        }

        mainGraph {
            profileScreen(
                onNavigateToSignIn = {
                    navController.navigateToAuth()
                },
            )
            orderGraph {
                storeListScreen(
                    navigateToStore = { storeId ->
                        navController.navigateToStore(storeId)
                    }
                )
                storeScreen(navController::navigateUp) {
                    navController.navigateToAuth()
                }
            }
            myPageScreen(
                navigateToStoreList = {
                    navController.navigateToOrder()
                },
                navigateToStore = { storeId ->
                    navController.navigateToStore(storeId)
                },
            )
        }
    }
    LaunchedEffect(shouldRedirectToAuth) {
        if (shouldRedirectToAuth) {
            navController.navigateToAuth()
        }
    }
}
