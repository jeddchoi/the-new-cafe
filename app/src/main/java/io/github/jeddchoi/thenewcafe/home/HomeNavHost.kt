package io.github.jeddchoi.thenewcafe.home

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import io.github.jeddchoi.account.AccountNavigation
import io.github.jeddchoi.account.accountScreen
import io.github.jeddchoi.mypage.myPageScreen
import io.github.jeddchoi.mypage.navigateToMyPage
import io.github.jeddchoi.store.navigateToStore
import io.github.jeddchoi.store.storeScreen
import io.github.jeddchoi.store_list.navigateToOrder
import io.github.jeddchoi.store_list.orderGraph


@Composable
fun HomeNavHost(
    navController: NavHostController,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    startDestination: String = AccountNavigation.route(),
) {

    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier,
    ) {
        accountScreen(
            onNavigateToSignIn = {
//                    navController.navigateToSignIn(needToRedirectSignIn)
            },
            onBackClick = onBackClick,
        )
        orderGraph(
            onNavigateToSignIn = {
//                    navController.navigateToSignIn(needToRedirectSignIn)
            },
            navigateToStore = { storeId ->
                navController.navigateToStore(storeId)
            },
            navigateToMyStatus = {
                navController.navigateToMyPage()
            },
            nestedGraphs = { storeScreen() },
            onBackClick = onBackClick
        )
        myPageScreen(
            onNavigateToSignIn = {
//                    navController.navigateToSignIn(needToRedirectSignIn)
            },
            navigateToStoreList = {
                navController.navigateToOrder()
            },
            navigateToStore = { storeId ->
                navController.navigateToStore(storeId)
            },
            onBackClick = onBackClick,
        )
    }
}