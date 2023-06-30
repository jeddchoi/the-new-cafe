package io.github.jeddchoi.thenewcafe.navigation.main

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.navOptions
import io.github.jeddchoi.mypage.navigateToMyPage
import io.github.jeddchoi.order.navigateToOrder
import io.github.jeddchoi.profile.navigateToProfile
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
//        profileScreen(
//            onNavigateToSignIn = navigateToSignIn,
//            navController = navController,
//            screens = screens,
//            onBottomNavClick = {
//                navigate(navController, it as MainNavScreen)
//            }
//        )
//        orderGraph(
//            navController = navController,
//            screens = screens,
//            onBottomNavClick = {
//                navigate(navController, it as MainNavScreen)
//            }
//        )
//        myPageScreen(
//            navController = navController,
//            screens = screens,
//            onBottomNavClick = {
//                navigate(navController, it as MainNavScreen)
//            },
//            onNavigateToSignIn = navigateToSignIn,
//            navigateToStoreList = {
//                navController.navigateToOrder()
//            },
//            navigateToStore = { storeId ->
//                navController.navigateToStore(storeId)
//            },
//        )
    }

    LaunchedEffect(Unit) {
        navController.currentBackStackEntryFlow.collect {
            Log.i("MainNavGraph", it.destination.hierarchy.joinToString("\n"))
        }
    }
}

fun navigate(navController: NavHostController, navScreen: MainNavScreen) {
    val navOptions = navOptions {
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
    }
    when (navScreen) {
        MainNavScreen.MyPage -> navController.navigateToMyPage(navOptions)
        MainNavScreen.Order -> navController.navigateToOrder(navOptions)
        MainNavScreen.Profile -> navController.navigateToProfile(navOptions)
    }
}