package io.github.jeddchoi.thenewcafe.navigation.main

import android.util.Log
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavOptions
import androidx.navigation.compose.navigation
import io.github.jeddchoi.designsystem.CafeNavigationBar
import io.github.jeddchoi.designsystem.CafeNavigationBarItem
import io.github.jeddchoi.designsystem.Icon
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
        startDestination = MainNavScreen.Profile.route,
        route = RootNavScreen.Main.route,
    ) {
        profileScreen(
            bottomBar = {
                BottomBar(navController)
            },
            onNavigateToSignIn = navigateToSignIn,
            navController = navController,
        )
        orderGraph(
            bottomBar = {
                BottomBar(navController)
            },
            navController = navController,
        )
        myPageScreen(
            navController = navController,
            bottomBar = {
                BottomBar(navController)
            },
            onNavigateToSignIn = navigateToSignIn,
            navigateToStoreList = {
                navController.navigateToOrder()
            },
            navigateToStore = { storeId ->
                navController.navigateToStore(storeId)
            },
        )
    }
//    composable(
//        route = MainRoutePattern,
//    ) {
//        val mainState = rememberMainState(navController = navController)
//        MainScreen(mainState = mainState, navigateToSignIn = navigateToSignIn)
//    }
}

@Composable
fun BottomBar(navHostController: NavHostController) {
    val screens = listOf(MainNavScreen.Profile, MainNavScreen.Order, MainNavScreen.MyPage)
    Log.i("Here", "1screens : ${screens.joinToString()}")
    BottomBar(screens = screens, currentDestination = navHostController.currentDestination) {
        navigate(navHostController, it)
    }
}

@Composable
fun BottomBar(
    screens: List<MainNavScreen>,
    currentDestination: NavDestination?,
    onBottomNavClick: (MainNavScreen) -> Unit
) {
    CafeNavigationBar {
        Log.i("Here", "2screens : ${screens.joinToString()}")
        screens.forEach { destination ->
            Log.i("Here", "3destination: $destination")
            val selected = currentDestination.isTopLevelDestinationInHierarchy(destination.route)
            CafeNavigationBarItem(
                selected = selected,
                onClick = { onBottomNavClick(destination) },
                icon = {
                    val icon = if (selected) {
                        destination.selectedIcon
                    } else {
                        destination.unselectedIcon
                    }
                    when (icon) {
                        is Icon.ImageVectorIcon -> Icon(
                            imageVector = icon.imageVector,
                            contentDescription = null,
                        )

                        is Icon.DrawableResourceIcon -> Icon(
                            painter = painterResource(id = icon.id),
                            contentDescription = null,
                        )
                    }
                },
                label = { Text(stringResource(destination.titleId)) },
            )

        }
    }

}

private fun NavDestination?.isTopLevelDestinationInHierarchy(route: String) =
    this?.hierarchy?.any { it.route?.contains(route, true) ?: false } ?: false