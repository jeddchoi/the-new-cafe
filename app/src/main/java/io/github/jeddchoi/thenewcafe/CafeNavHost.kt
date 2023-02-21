package io.github.jeddchoi.thenewcafe

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import io.github.jeddchoi.account.accountRoute
import io.github.jeddchoi.account.accountScreen
import io.github.jeddchoi.mypage.myPageScreen
import io.github.jeddchoi.store.navigateToStore
import io.github.jeddchoi.store.storeScreen
import io.github.jeddchoi.order.orderGraph

@Composable
fun CafeNavHost(
    navController: NavHostController,
    onBackClick: () -> Unit = {},
    modifier: Modifier = Modifier,
    startDestination: String = accountRoute,
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        accountScreen()
        orderGraph(
            navigateToStore = {storeId ->
                navController.navigateToStore(storeId)
            },
            nestedGraphs = {
                storeScreen()
            }
        )
        myPageScreen()
    }
}