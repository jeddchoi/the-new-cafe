package io.github.jeddchoi.thenewcafe.ui.main

import android.content.Intent
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.navigation
import androidx.navigation.navDeepLink
import timber.log.Timber

internal const val MainRoutePattern = "main"

fun NavController.navigateToMain(navOptions: NavOptions? = null) {
    Timber.v("✅ $navOptions")
    this.navigate(MainRoutePattern, navOptions)
}

fun NavGraphBuilder.mainGraph(
    nestedGraphs: NavGraphBuilder.() -> Unit,
) {
    navigation(
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
        startDestination = MainBottomNav.Profile.route,
    ) {
        nestedGraphs()
    }
}
