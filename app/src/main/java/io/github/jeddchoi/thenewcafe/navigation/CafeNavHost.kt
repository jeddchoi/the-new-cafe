package io.github.jeddchoi.thenewcafe.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import io.github.jeddchoi.account.AccountNavigation
import io.github.jeddchoi.account.accountScreen
import io.github.jeddchoi.mypage.myPageScreen
import io.github.jeddchoi.store.navigateToStore
import io.github.jeddchoi.store.storeScreen
import io.github.jeddchoi.store_list.orderGraph
import io.github.jeddchoi.ui.LogCompositions


@Composable
fun CafeNavHost(
    navController: NavHostController,
    shouldHandleReselection: Boolean,
    modifier: Modifier = Modifier,
    onHandleReselection: () -> Unit,
    onBackClick: () -> Unit = {},
    onShowMyStatus: () -> Unit = {},
    onShowActionLog: () -> Unit = {},
    startDestination: String = AccountNavigation.route(),
) {

    LogCompositions(
        tag = "TAG",
        msg = "backQUEUE\n${ navController.backQueue.joinToString("\n->") { "[${it.id}] ${it.destination} / ${it.arguments}" } }"
    )
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        accountScreen(
            onShowMyStatus = onShowMyStatus,
            onShowActionLog = onShowActionLog,
        )
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