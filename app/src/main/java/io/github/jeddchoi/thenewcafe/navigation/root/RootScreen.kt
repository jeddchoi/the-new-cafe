package io.github.jeddchoi.thenewcafe.navigation.root

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import io.github.jeddchoi.data.util.ConnectedState
import io.github.jeddchoi.data.util.NetworkMonitor
import io.github.jeddchoi.designsystem.component.lottie.ConfettiLottie
import io.github.jeddchoi.thenewcafe.R
import io.github.jeddchoi.thenewcafe.navigation.main.BottomBar

/**
 * Single entry point of composable world
 *
 * @param rootState : state holder of app
 */
@Composable
fun RootScreen(
    windowSizeClass: WindowSizeClass,
    networkMonitor: NetworkMonitor,
    navController: NavHostController,
    modifier: Modifier = Modifier,
    rootState: RootState = rememberRootState(
        windowSizeClass = windowSizeClass,
        networkMonitor = networkMonitor,
        navController = navController,
    ),
    startDestination: RootNavScreen = RootNavScreen.Auth,
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
                        currentDestination = rootState.currentDestination
                    )
                }
                ConnectionState(connectedState)
            }
        },
    ) { innerPadding ->
        Box(
            modifier = modifier.padding(innerPadding),
            contentAlignment = Alignment.Center
        ) {
            RootNavGraph(
                navController = rootState.navController,
                startDestination = startDestination,
            )

            ConfettiLottie(
                Modifier.matchParentSize()
            )
        }
    }
}

@Composable
private fun ConnectionState(connectedState: ConnectedState) {
    when (connectedState) {
        ConnectedState.CONNECTED -> {}
        ConnectedState.FOUND_CONNECTION -> {
            val connectingMessage = stringResource(R.string.connected_again)
            Surface(
                color = colorResource(id = R.color.positive_background),
            ) {
                Text(
                    text = connectingMessage,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                )
            }
        }
        ConnectedState.LOST -> {
            val notConnectedMessage = stringResource(R.string.not_connected)
            Text(
                text = notConnectedMessage,
                color = MaterialTheme.colorScheme.error,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.errorContainer)
            )
        }
    }
}