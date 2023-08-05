package io.github.jeddchoi.thenewcafe.ui.root

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import io.github.jeddchoi.data.util.NetworkMonitor
import io.github.jeddchoi.designsystem.component.lottie.ConfettiLottie
import io.github.jeddchoi.thenewcafe.ui.main.BottomBar

/**
 * Single entry point of composable world
 *
 * @param rootState : state holder of app
 */
@Composable
fun RootScreen(
    windowSizeClass: WindowSizeClass,
    networkMonitor: NetworkMonitor,
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    rootState: RootState = rememberRootState(
        windowSizeClass = windowSizeClass,
        networkMonitor = networkMonitor,
        navController = navController,
    ),
    showConfetti: Boolean = false,
) {
    val showBottomBar by rootState.showBottomBar.collectAsStateWithLifecycle(initialValue = false)
    val connectedState by rootState.connectedState.collectAsStateWithLifecycle()

    Scaffold(
        modifier = modifier,
        bottomBar = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (showBottomBar) {
                    BottomBar(
                        navController = rootState.navController,
                        currentDestination = rootState.currentDestination,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                ConnectionState(connectedState)
            }
        },
    ) { innerPadding ->
        Box(
            modifier = Modifier.padding(innerPadding),
            contentAlignment = Alignment.Center
        ) {

            RootNavGraph(
                modifier = Modifier.fillMaxSize(),
                navController = rootState.navController,
            )
            if (showConfetti) {
                ConfettiLottie(
                    modifier = Modifier.matchParentSize(),
                )
            }
        }
    }
}

