package io.github.jeddchoi.thenewcafe

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import io.github.jeddchoi.designsystem.*
import io.github.jeddchoi.thenewcafe.navigation.CafeNavHost
import io.github.jeddchoi.ui.LogCompositions
import io.github.jeddchoi.ui.feature.AppNavigation

/**
 * Single entry point of composable world
 *
 * @param appState : state holder of app
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CafeApp(
    modifier: Modifier = Modifier,
    appState: CafeAppState = rememberCafeAppState()
) {
    LogCompositions(tag = "TAG", msg = "CafeApp")
    Scaffold(
        modifier = modifier.fillMaxSize(),
        bottomBar = {
            CafeBottomBar(
                destinations = appState.topLevelDestinations,
                onNavigateToDestination = {
                    appState.navController.navigateToSingleTopDestination(it)
                },
                onReselection = appState::setHandleReselection,
                currentDestination = appState.currentDestination,
            )
        },
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            val destination = appState.currentAppDestination

            if (destination != null) {

                CafeTopAppBar(
                    titleRes = destination.titleTextId,
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = Color.Transparent,
                    ),
                )
            }
            CafeNavHost(
                navController = appState.navController,
                shouldHandleReselection = appState.shouldHandleReselection,
                onHandleReselection = { appState.setHandleReselection(false) },
                onBackClick = appState::onBackClick,
                onShowActionLog = appState::navigateToActionLog
            )
        }
    }
}

@Composable
private fun CafeBottomBar(
    destinations: List<AppNavigation>,
    onNavigateToDestination: (AppNavigation) -> Unit,
    onReselection: (Boolean) -> Unit,
    currentDestination: NavDestination?,
    modifier: Modifier = Modifier,
) {
    CafeNavigationBar(
        modifier = modifier,
    ) {
        destinations.forEach { destination ->
            val selected = currentDestination.isTopLevelDestinationInHierarchy(destination)
            CafeNavigationBarItem(
                selected = selected,
                onClick = {
                    if (selected)
                        onReselection(true)
                    else
                        onNavigateToDestination(destination)
                },
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
                label = { Text(stringResource(destination.iconTextId)) },
            )
        }
    }
}

private fun NavDestination?.isTopLevelDestinationInHierarchy(destination: AppNavigation) =
    this?.hierarchy?.any {
        it.route?.contains(destination.name, true) ?: false
    } ?: false


