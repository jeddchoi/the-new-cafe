package io.github.jeddchoi.thenewcafe

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import io.github.jeddchoi.account.accountRoute
import io.github.jeddchoi.account.accountScreen

@Composable
fun CafeNavHost(
    navController: NavHostController,
    onBackClick: () -> Unit = {},
    modifier: Modifier = Modifier,
    startDestination: String = accountRoute,
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        accountScreen()

    }
}