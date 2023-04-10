package io.github.jeddchoi.thenewcafe

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import io.github.jeddchoi.thenewcafe.navigation.AppNavHost
import io.github.jeddchoi.ui.feature.BottomNavigation

/**
 * Single entry point of composable world
 *
 * @param appState : state holder of app
 */
@Composable
fun CafeApp(
    modifier: Modifier = Modifier,
    appState: CafeAppState = rememberCafeAppState()
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        // TODO: Add lottie animation

        AppNavHost(
            navController = appState.navController,
            onBackClick = appState::onBackClick
        )
    }
}


fun NavDestination?.isTopLevelDestinationInHierarchy(destination: BottomNavigation) =
    this?.hierarchy?.any { it.route?.contains(destination.name, true) ?: false } ?: false


