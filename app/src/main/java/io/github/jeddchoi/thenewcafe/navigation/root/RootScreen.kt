package io.github.jeddchoi.thenewcafe.navigation.root

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.jeddchoi.designsystem.component.lottie.ConfettiLottie
import io.github.jeddchoi.thenewcafe.navigation.main.BottomBar

/**
 * Single entry point of composable world
 *
 * @param rootState : state holder of app
 */
@Composable
fun RootScreen(
    modifier: Modifier = Modifier,
    rootState: RootState = rememberRootState(),
    startDestination: RootNavScreen = RootNavScreen.Auth,
) {
    val showBottomBar by rootState.showNavigation.collectAsStateWithLifecycle(initialValue = false)

    Scaffold(
        modifier = modifier,
        bottomBar = {
            if (showBottomBar) {
                BottomBar(
                    navController = rootState.navController,
                    currentDestination = rootState.currentDestination
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = modifier.padding(innerPadding).border(4.dp, MaterialTheme.colorScheme.tertiary),
            contentAlignment = Alignment.Center
        ) {
            RootNavGraph(
                navController = rootState.navController,
                onBackClick = rootState::onBackClick,
                startDestination = startDestination,
                modifier = modifier
            )
            ConfettiLottie(
                Modifier
                    .matchParentSize()
                    .border(8.dp, MaterialTheme.colorScheme.primary)
            )
        }
    }
}