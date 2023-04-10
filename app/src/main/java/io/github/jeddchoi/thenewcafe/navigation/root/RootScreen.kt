package io.github.jeddchoi.thenewcafe.navigation.root

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import io.github.jeddchoi.authentication.SigninNavigation

/**
 * Single entry point of composable world
 *
 * @param rootState : state holder of app
 */
@Composable
fun RootScreen(
    modifier: Modifier = Modifier,
    rootState: RootState = rememberRootState(),
    startDestination: String = SigninNavigation.routeGraph,
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        // TODO: Add lottie animation

        RootNavGraph(
            navController = rootState.navController,
            onBackClick = rootState::onBackClick,
            startDestination = startDestination
        )
    }
}