package io.github.jeddchoi.thenewcafe.navigation.root

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController

/**
 * Single entry point of composable world
 *
 * @param rootState : state holder of app
 */
@Composable
fun RootScreen(
    modifier: Modifier = Modifier,
    rootState: RootState = rememberRootState(),
    mainNavController: NavHostController = rememberNavController(),
    startDestination: RootNavScreen = RootNavScreen.Auth,
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        // TODO: Add lottie animation

        RootNavGraph(
            rootNavController = rootState.navController,
            mainNavController = mainNavController,
            onBackClick = rootState::onBackClick,
            startDestination = startDestination
        )
    }
}