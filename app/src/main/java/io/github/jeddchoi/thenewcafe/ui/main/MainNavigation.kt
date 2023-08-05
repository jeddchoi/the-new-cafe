package io.github.jeddchoi.thenewcafe.ui.main

import android.content.Intent
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.navDeepLink
import io.github.jeddchoi.data.util.NetworkMonitor
import io.github.jeddchoi.thenewcafe.ui.findActivity
import timber.log.Timber

internal const val MainRoutePattern = "main"

fun NavController.navigateToMain(navOptions: NavOptions? = null) {
    Timber.v("✅ $navOptions")
    this.navigate(MainRoutePattern, navOptions)
}

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
fun NavGraphBuilder.mainScreen(
    networkMonitor: NetworkMonitor,
    navigateToAuth: () -> Unit = {},
    navigateToHistoryDetail: (String) -> Unit = {},
) {
    composable(
        route = MainRoutePattern,
        deepLinks = listOf(
            navDeepLink {
                uriPattern = "https://io.github.jeddchoi.thenewcafe"
                action = Intent.ACTION_VIEW
            },
            navDeepLink {
                uriPattern = "jeddchoi://thenewcafe"
                action = Intent.ACTION_VIEW
            }
        ),
    ) {
        MainScreen(
            windowSizeClass = calculateWindowSizeClass(LocalContext.current.findActivity()),
            networkMonitor = networkMonitor,
            navigateToAuth = navigateToAuth,
            navigateToHistoryDetail = navigateToHistoryDetail,
        )
    }
}
