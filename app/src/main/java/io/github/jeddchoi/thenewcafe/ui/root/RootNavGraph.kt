package io.github.jeddchoi.thenewcafe.ui.root

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
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
    modifier: Modifier = Modifier,
    startDestination: RootNav = RootNav.Auth
) {
    NavHost(
        navController = navController,
        startDestination = startDestination.route,
        modifier = modifier,
    ) {

        authGraph {
            val navigateToMain = {
                navController.navigateToMain(navOptions = navOptions {
                    popUpTo(RootNav.Auth.route) {
                        inclusive = true
                    }
                    launchSingleTop = true
                })
            }
            signInScreen(navController, navigateToMain)
            registerScreen(navController, navigateToMain)
        }

        mainGraph {
            profileScreen(
                onNavigateToSignIn = {
                    navController.navigateToAuth(navOptions = navOptions {
                        popUpTo(RootNav.Main.route) {
                            inclusive = true
                        }
                        launchSingleTop = true
                    })
                },
            )
            orderGraph {
                storeListScreen()
                storeScreen()
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
}