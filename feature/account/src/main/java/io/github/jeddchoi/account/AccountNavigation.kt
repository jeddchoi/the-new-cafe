package io.github.jeddchoi.account

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable


const val accountRoute = "account"
fun NavGraphBuilder.accountScreen() {
    composable(route = accountRoute) {
        AccountRoute()
    }
}