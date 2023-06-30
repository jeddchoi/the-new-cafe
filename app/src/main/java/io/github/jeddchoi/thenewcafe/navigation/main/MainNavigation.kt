package io.github.jeddchoi.thenewcafe.navigation.main

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavOptions
import androidx.navigation.compose.navigation
import io.github.jeddchoi.mypage.myPageScreen
import io.github.jeddchoi.order.navigateToOrder
import io.github.jeddchoi.order.orderGraph
import io.github.jeddchoi.order.store.navigateToStore
import io.github.jeddchoi.profile.profileScreen
import io.github.jeddchoi.thenewcafe.navigation.root.RootNavScreen

private const val MainRoutePattern = "main"

fun NavController.navigateToMain(navOptions: NavOptions? = null) {
    this.navigate(MainRoutePattern, navOptions)
}

fun NavGraphBuilder.mainScreen(
    navController: NavHostController,
    navigateToSignIn: () -> Unit,
) {
    navigation(
        startDestination = MainBottomNav.Profile.route,
        route = RootNavScreen.Main.route,
    ) {

        profileScreen(
            navController = navController,
            onNavigateToSignIn = navigateToSignIn,
        )
        orderGraph(
            navController = navController,
        )
        myPageScreen(
            navController = navController,
            onNavigateToSignIn = navigateToSignIn,
            navigateToStoreList = {
                navController.navigateToOrder()
            },
            navigateToStore = { storeId ->
                navController.navigateToStore(storeId)
            },
        )
    }
}
