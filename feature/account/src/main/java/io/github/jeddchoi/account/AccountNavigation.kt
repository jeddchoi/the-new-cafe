package io.github.jeddchoi.account

import android.util.Log
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.navDeepLink
import io.github.jeddchoi.ui.LogCompositions


const val accountRoute = "account"

fun NavController.navigateToAccount(navOptions: NavOptions? = null) {
    Log.i("TAG", "Navigate to Account")
    this.navigate(accountRoute, navOptions)
}

fun NavGraphBuilder.accountScreen(
    baseWebUri: String,
    baseAppUri: String,
    onShowActionLog: () -> Unit = {},
) {

    composable(
        route = accountRoute,
        deepLinks = listOf(
            navDeepLink { uriPattern = "$baseWebUri/$accountRoute" },
            navDeepLink { uriPattern = "$baseAppUri/$accountRoute" }
        )
    ) {backStackEntry ->
        LogCompositions(tag = "TAG", msg = "Account : backStackEntry = ${backStackEntry.arguments}")
        AccountRoute(onShowActionLog = onShowActionLog)
    }
}
