package io.github.jeddchoi.ui.component

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun ScreenWithBottomBar(
//    screens: List<NavScreen>,
//    currentDestination: NavDestination?,
//    onBottomNavClick: (NavScreen) -> Unit,
    bottomBar: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable (Modifier) -> Unit
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        bottomBar = bottomBar,
    ) { padding ->
        content(Modifier.padding(padding))
    }
}


