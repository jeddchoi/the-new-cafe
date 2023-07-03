package io.github.jeddchoi.thenewcafe.navigation.main

import android.content.Intent
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavOptions
import androidx.navigation.compose.navigation
import androidx.navigation.navDeepLink
import io.github.jeddchoi.mypage.myPageScreen
import io.github.jeddchoi.order.navigateToOrder
import io.github.jeddchoi.order.orderGraph
import io.github.jeddchoi.order.store.navigateToStore
import io.github.jeddchoi.profile.profileScreen

private const val MainRoutePattern = "main"

fun NavController.navigateToMain(navOptions: NavOptions? = null) {
    this.navigate(MainRoutePattern, navOptions)
}

fun NavGraphBuilder.mainScreen(
    navController: NavHostController,
    navigateToSignIn: () -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    navigation(
        
        route = MainRoutePattern,
        deepLinks = listOf(
            navDeepLink {
                uriPattern = "https://io.github.jeddchoi.thenewcafe"
                action = Intent.ACTION_VIEW
            },
            navDeepLink {
                uriPattern = "jeddchoi://thenewcafe"
                action = Intent.ACTION_VIEW
            }
        ),
        startDestination = MainBottomNav.Profile.route,
    ) {

        profileScreen(
            navController = navController,
            onNavigateToSignIn = navigateToSignIn,
            modifier = modifier 
        )
        orderGraph(
            navController = navController,
            modifier = modifier
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
            modifier = modifier
        )
    }
}
