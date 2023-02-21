package io.github.jeddchoi.account

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable


const val accountRoute = "account_route"
fun NavGraphBuilder.accountScreen() {
    composable(route = accountRoute) {
        AccountRoute()
    }
}
