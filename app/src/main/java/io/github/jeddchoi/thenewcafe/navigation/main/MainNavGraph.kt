package io.github.jeddchoi.thenewcafe.navigation.main

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import io.github.jeddchoi.profile.ProfileNavigation
import io.github.jeddchoi.profile.profileScreen
import io.github.jeddchoi.mypage.myPageScreen
import io.github.jeddchoi.mypage.navigateToMyPage
import io.github.jeddchoi.store.navigateToStore
import io.github.jeddchoi.store.storeScreen
import io.github.jeddchoi.store_list.navigateToOrder
import io.github.jeddchoi.store_list.orderGraph


@Composable
fun MainNavGraph(
    navController: NavHostController,
    onBackClick: () -> Unit,
    navigateToSignIn: () -> Unit,
    modifier: Modifier = Modifier,
    startDestination: String = ProfileNavigation.route(),
) {

    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier,
    ) {
        profileScreen(
            onNavigateToSignIn = navigateToSignIn,
            onBackClick = onBackClick,
        )
        orderGraph(
            onNavigateToSignIn = navigateToSignIn,
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
}