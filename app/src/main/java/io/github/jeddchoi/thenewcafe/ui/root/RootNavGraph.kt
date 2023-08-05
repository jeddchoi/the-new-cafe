package io.github.jeddchoi.thenewcafe.ui.root

import androidx.compose.runtime.Composable
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
import io.github.jeddchoi.historydetail.historyDetailScreen
import io.github.jeddchoi.historydetail.navigateToHistoryDetail
import io.github.jeddchoi.mypage.myPageGraph
import io.github.jeddchoi.mypage.myPageScreen
import io.github.jeddchoi.mypage.navigateToMyPage
import io.github.jeddchoi.order.navigateToOrder
import io.github.jeddchoi.order.orderGraph
import io.github.jeddchoi.order.store.navigateToStore
import io.github.jeddchoi.order.store.storeScreen
import io.github.jeddchoi.order.store_list.storeListScreen
import io.github.jeddchoi.profile.profileGraph
import io.github.jeddchoi.profile.profileScreen
import io.github.jeddchoi.thenewcafe.ui.main.MainRoutePattern
import io.github.jeddchoi.thenewcafe.ui.main.mainGraph
import io.github.jeddchoi.thenewcafe.ui.main.navigateToMain

@Composable
fun RootNavGraph(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
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
            val navigateToAuth = {
                navController.navigateToAuth(navOptions = navOptions {
                    popUpTo(navController.graph.findStartDestination().id)
                    launchSingleTop = true
                })
            }
            profileGraph {
                profileScreen(
                    navigateToAuth = navigateToAuth,
                )
            }

            orderGraph {
                storeListScreen(
                    navigateToStore = navController::navigateToStore
                )
                storeScreen(
                    onBackClick = navController::navigateUp,
                    navigateToAuth = navigateToAuth,
                    navigateToMyPage = {
                        navController.navigateToMyPage(navOptions {
                            // Pop up to the start destination of the graph to
                            // avoid building up a large stack of destinations
                            // on the back stack as users select items
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            // Avoid multiple copies of the same destination when
                            // reselecting the same item
                            launchSingleTop = true

                            // Restore state when reselecting a previously selected item
                            restoreState = true
                        })
                    },
                )
            }
            myPageGraph {
                myPageScreen(
                    navigateToStoreList = navController::navigateToOrder,
                    navigateToStore = navController::navigateToStore,
                    navigateToHistoryDetail = navController::navigateToHistoryDetail,
                    navigateToSignIn = navigateToAuth
                )
            }
        }

        historyDetailScreen(
            clickBack = navController::popBackStack
        )
    }

}
