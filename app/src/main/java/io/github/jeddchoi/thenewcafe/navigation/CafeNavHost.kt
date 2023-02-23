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
import io.github.jeddchoi.ui.LogCompositions


const val webUri = "https://www.example.com"
const val appUri = "jeddchoi://thenewcafe"

@Composable
fun CafeNavHost(
    navController: NavHostController,
    shouldHandleReselection: Boolean,
    onHandleReselection: () -> Unit,
    onBackClick: () -> Unit = {},
    modifier: Modifier = Modifier,
    startDestination: String = accountRoute,
) {
    LogCompositions(tag = "TAG", msg = "CafeNavHost")
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        accountScreen(
            baseWebUri = webUri,
            baseAppUri = appUri,
        )
        orderGraph(
            baseWebUri = webUri,
            baseAppUri = appUri,
            navigateToStore = { storeId ->
                navController.navigateToStore(storeId)
            },
            nestedGraphs = {
                storeScreen(
                    baseWebUri = webUri,
                    baseAppUri = appUri,
                )
            }
        )
        myPageScreen(
            baseWebUri = webUri,
            baseAppUri = appUri,
            shouldHandleReselection = shouldHandleReselection,
            onHandleReselection = onHandleReselection
        )
    }
}