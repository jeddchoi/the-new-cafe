package io.github.jeddchoi.thenewcafe.home

import androidx.annotation.StringRes
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavDeepLink
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navDeepLink
import io.github.jeddchoi.thenewcafe.R
import io.github.jeddchoi.ui.feature.AppNavigation
import io.github.jeddchoi.ui.feature.baseAppUri
import io.github.jeddchoi.ui.feature.baseWebUri

object HomeNavigation : AppNavigation {
    override val name: String = "home"

    @StringRes
    override val titleTextId: Int = R.string.app_name
    override fun route(arg: String?): String = name

    override val arguments: List<NamedNavArgument> = listOf()
    override val deepLinks: List<NavDeepLink> = listOf(
        navDeepLink { uriPattern = "$baseWebUri/${route()}" },
        navDeepLink { uriPattern = "$baseAppUri/${route()}" }
    )

}

fun NavGraphBuilder.homeScreen(
    onBackClick: () -> Unit,
) {
    composable(
        route = HomeNavigation.route(),
        deepLinks = HomeNavigation.deepLinks,
    ) {
        val homeState = rememberHomeState()
        HomeScreen(
            homeState = homeState,
            onBackClick = onBackClick,
        )
    }
}
