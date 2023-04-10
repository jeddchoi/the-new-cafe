package io.github.jeddchoi.thenewcafe.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavDestination
import io.github.jeddchoi.designsystem.CafeNavigationBar
import io.github.jeddchoi.designsystem.CafeNavigationBarItem
import io.github.jeddchoi.designsystem.CafeTopAppBar
import io.github.jeddchoi.designsystem.Icon
import io.github.jeddchoi.thenewcafe.isTopLevelDestinationInHierarchy
import io.github.jeddchoi.ui.feature.BottomNavigation

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    homeState: HomeState,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        bottomBar = {
            CafeBottomBar(
                bottomNavigations = homeState.bottomNavigations,
                currentDestination = homeState.currentDestination,
                selectBottomNav = {
                    homeState.onNavigateToBottomNav(it)
                },
            )
        },
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            val destination = homeState.currentTopLevelBottomNav

            // if the top level destination is not null, we're in the home screen
            if (destination != null) {
                CafeTopAppBar(
                    titleRes = destination.titleTextId,
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = Color.Transparent,
                    ),
                )
            }
            HomeNavHost(
                navController = homeState.navController,
                onBackClick = onBackClick,
            )
        }
    }
}

@Composable
private fun CafeBottomBar(
    bottomNavigations: List<BottomNavigation>,
    currentDestination: NavDestination?,
    selectBottomNav: (BottomNavigation) -> Unit,
    modifier: Modifier = Modifier,
) {
    CafeNavigationBar(
        modifier = modifier.fillMaxWidth(),
    ) {
        bottomNavigations.forEach { destination ->

            val selected = currentDestination.isTopLevelDestinationInHierarchy(destination)


            CafeNavigationBarItem(
                selected = selected,
                onClick = {
                    selectBottomNav(destination)
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