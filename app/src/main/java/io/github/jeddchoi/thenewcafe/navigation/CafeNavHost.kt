package io.github.jeddchoi.thenewcafe.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import io.github.jeddchoi.account.accountRoute
import io.github.jeddchoi.account.accountScreen
import io.github.jeddchoi.mypage.myPageScreen
import io.github.jeddchoi.order.orderGraph
import io.github.jeddchoi.store.navigateToStore
import io.github.jeddchoi.store.storeScreen

@Composable
fun CafeNavHost(
    navController: NavHostController,
    shouldHandleReselection: Boolean,
    onHandleReselection: () -> Unit,
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
            navigateToStore = { storeId ->
                navController.navigateToStore(storeId)
            },
            nestedGraphs = {
                storeScreen()
            }
        )
        myPageScreen(
            shouldHandleReselection = shouldHandleReselection,
            onHandleReselection = onHandleReselection
        )
    }
}