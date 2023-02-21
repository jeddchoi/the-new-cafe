package io.github.jeddchoi.account

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable


const val accountRoute = "account_route"

fun NavController.navigateToAccount(navOptions: NavOptions? = null) {
    this.navigate(accountRoute, navOptions)
}

fun NavGraphBuilder.accountScreen() {
    composable(route = accountRoute) {
        AccountRoute()
    }
}
