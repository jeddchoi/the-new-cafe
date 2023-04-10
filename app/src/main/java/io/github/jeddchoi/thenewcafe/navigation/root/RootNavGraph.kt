package io.github.jeddchoi.thenewcafe.navigation.root

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import io.github.jeddchoi.authentication.authGraph
import io.github.jeddchoi.thenewcafe.navigation.main.MainNavigation
import io.github.jeddchoi.thenewcafe.navigation.main.mainScreen

@Composable
fun RootNavGraph(
    navController: NavHostController,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    startDestination: String = MainNavigation.route()
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier,
    ) {

        authGraph(onBackClick = onBackClick) {

        }

        mainScreen()
    }
}



