package io.github.jeddchoi.thenewcafe.ui.main

import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import androidx.navigation.NavDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import io.github.jeddchoi.data.util.ConnectedState
import io.github.jeddchoi.data.util.NetworkMonitor
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.runningFold
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.transform
import timber.log.Timber
import kotlin.time.Duration.Companion.seconds

@Composable
fun rememberMainState(
    windowSizeClass: WindowSizeClass,
    networkMonitor: NetworkMonitor,
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
    navController: NavHostController = rememberNavController(),
): MainState {
    return remember(navController, coroutineScope, windowSizeClass, networkMonitor) {
        MainState(navController, coroutineScope, windowSizeClass, networkMonitor)
    }
}


@Stable
class MainState(
    val navController: NavHostController,
    val coroutineScope: CoroutineScope,
    val windowSizeClass: WindowSizeClass,
    val networkMonitor: NetworkMonitor
) {
    val currentDestination: NavDestination?
        @Composable get() = navController.currentBackStackEntryAsState().value?.destination


    private val isCompact =
        snapshotFlow { windowSizeClass.widthSizeClass == WindowWidthSizeClass.Compact }

    val connectedState = networkMonitor.isOnline
        .map(Boolean::not) // isOffline
        .runningFold(
            Pair(false, false),
            operation = { accumulator, new -> Pair(accumulator.second, new) }
        ).transform { (prev, cur) ->
            if (cur) { // current is offline
                emit(ConnectedState.LOST)
            } else { // current is online
                if (prev) { // previous was offline
                    emit(ConnectedState.FOUND_CONNECTION)
                    delay(3.seconds)
                    emit(ConnectedState.CONNECTED)
                } else { // previous was online
                    emit(ConnectedState.CONNECTED)
                }
            }
        }
        .onEach { Timber.v("💥 $it") }
        .stateIn(
            scope = coroutineScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = ConnectedState.CONNECTED,
        )

}