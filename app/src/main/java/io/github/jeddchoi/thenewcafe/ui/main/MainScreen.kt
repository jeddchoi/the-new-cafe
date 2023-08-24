package io.github.jeddchoi.thenewcafe.ui.main

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
import io.github.jeddchoi.data.util.NetworkMonitor
import io.github.jeddchoi.thenewcafe.ui.root.ConnectionState
import io.github.jeddchoi.thenewcafe.ui.root.Redirection
import kotlinx.coroutines.flow.SharedFlow

@Composable
fun MainScreen(
    redirectionByNfcRead: SharedFlow<Redirection?>,
    windowSizeClass: WindowSizeClass,
    networkMonitor: NetworkMonitor,
    modifier: Modifier = Modifier,
    mainState: MainState = rememberMainState(
        windowSizeClass = windowSizeClass,
        networkMonitor = networkMonitor
    ),
    navigateToAuth: () -> Unit = {},
    navigateToHistoryDetail: (String) -> Unit = {},
) {
    val connectedState by mainState.connectedState.collectAsStateWithLifecycle()
    Scaffold(
        modifier = modifier,
        bottomBar = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                BottomBar(
                    navController = mainState.navController,
                    currentDestination = mainState.currentDestination,
                    modifier = Modifier.fillMaxWidth()
                )
                ConnectionState(connectedState)
            }
        },
    ) { innerPadding ->
        Box(
            modifier = Modifier.padding(innerPadding),
            contentAlignment = Alignment.Center
        ) {

            MainNavGraph(
                redirectionByNfcRead = redirectionByNfcRead,
                modifier = Modifier.fillMaxSize(),
                navController = mainState.navController,
                navigateToAuth = navigateToAuth,
                navigateToHistoryDetail = navigateToHistoryDetail,
            )
        }
    }
}


