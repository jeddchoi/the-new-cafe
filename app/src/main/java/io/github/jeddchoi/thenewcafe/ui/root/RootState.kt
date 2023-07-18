package io.github.jeddchoi.thenewcafe.ui.root

import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import io.github.jeddchoi.data.util.ConnectedState
import io.github.jeddchoi.data.util.NetworkMonitor
import io.github.jeddchoi.thenewcafe.ui.main.MainRoutePattern
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.runningFold
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.transform
import kotlin.time.Duration.Companion.seconds


@Composable
fun rememberRootState(
    windowSizeClass: WindowSizeClass,
    networkMonitor: NetworkMonitor,
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
    navController: NavHostController = rememberNavController(),
): RootState {
    return remember(navController, coroutineScope, windowSizeClass, networkMonitor, ) {
        RootState(navController, coroutineScope, windowSizeClass, networkMonitor, )
    }
}

@Stable
class RootState(
    val navController: NavHostController,
    val coroutineScope: CoroutineScope,
    val windowSizeClass: WindowSizeClass,
    networkMonitor: NetworkMonitor,
) {

    val currentDestination: NavDestination?
        @Composable get() = navController.currentBackStackEntryAsState().value?.destination

    private val isMainRoute = navController.currentBackStackEntryFlow.map { backStackEntry ->
        backStackEntry.destination.hierarchy.any {
            it.route?.contains(MainRoutePattern, true) ?: false
        }
    }

    private val isCompact = snapshotFlow { windowSizeClass.widthSizeClass == WindowWidthSizeClass.Compact }


    val showBottomBar = combine(isMainRoute, isCompact) {isMainRoute, isCompact ->
        isMainRoute && isCompact
    }
    
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
        .stateIn(
            scope = coroutineScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = ConnectedState.CONNECTED,
        )
}


