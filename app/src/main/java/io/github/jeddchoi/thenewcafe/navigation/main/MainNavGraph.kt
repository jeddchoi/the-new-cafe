package io.github.jeddchoi.thenewcafe.navigation.main

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import io.github.jeddchoi.mypage.myPageScreen
import io.github.jeddchoi.order.navigateToOrder
import io.github.jeddchoi.order.orderGraph
import io.github.jeddchoi.order.store.navigateToStore
import io.github.jeddchoi.profile.profileScreen
import io.github.jeddchoi.thenewcafe.navigation.root.RootNavScreen


@Composable
fun MainNavGraph(
    navController: NavHostController,
    onBackClick: () -> Unit,
    navigateToSignIn: () -> Unit,
    modifier: Modifier = Modifier,
) {
    NavHost(
        route = RootNavScreen.Main.route,
        navController = navController,
        startDestination = MainNavScreen.Profile.route,
        modifier = modifier,
    ) {
        profileScreen(
            onNavigateToSignIn = navigateToSignIn,
            onBackClick = onBackClick,
        )
        orderGraph(
            navController = navController
        )
        myPageScreen(
            onNavigateToSignIn = navigateToSignIn,
            navigateToStoreList = {
                navController.navigateToOrder()
            },
            navigateToStore = { storeId ->
                navController.navigateToStore(storeId)
            },
            onBackClick = onBackClick,
        )
    }

    LaunchedEffect(Unit) {
        navController.currentBackStackEntryFlow.collect {
            Log.i("MainNavGraph", it.destination.hierarchy.joinToString("\n"))
        }
    }
}