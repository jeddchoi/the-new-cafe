package io.github.jeddchoi.account

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.navDeepLink


const val accountRoute = "account_route"

fun NavController.navigateToAccount(navOptions: NavOptions? = null) {
    this.navigate(accountRoute, navOptions)
}

fun NavGraphBuilder.accountScreen(baseWebUri: String, baseAppUri: String) {
    composable(
        route = accountRoute,
        deepLinks = listOf(
            navDeepLink { uriPattern = "$baseWebUri/ACCOUNT" },
            navDeepLink { uriPattern = "$baseAppUri/ACCOUNT" }
        )
    ) {
        AccountRoute()
    }
}
