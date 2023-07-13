package io.github.jeddchoi.thenewcafe.navigation.main

import androidx.annotation.StringRes
import androidx.navigation.*
import androidx.navigation.compose.composable
import io.github.jeddchoi.thenewcafe.R
import io.github.jeddchoi.ui.feature.AppNavigation
import io.github.jeddchoi.ui.feature.AppNavigation.Companion.baseAppUri
import io.github.jeddchoi.ui.feature.AppNavigation.Companion.baseWebUri

object MainNavigation : AppNavigation {
    override val name: String = "main"

    @StringRes
    override val titleTextId: Int = R.string.app_name
    override fun route(arg: String?): String = name

    override val arguments: List<NamedNavArgument> = listOf()
    override val deepLinks: List<NavDeepLink> = listOf(
        navDeepLink { uriPattern = "$baseWebUri/${route()}" },
        navDeepLink { uriPattern = "$baseAppUri/${route()}" }
    )

}

fun NavController.navigateToMain(navOptions: NavOptions? = null) {
    this.navigate(MainNavigation.route(), navOptions)
}

fun NavGraphBuilder.mainScreen(navigateToSignIn: () -> Unit) {
    composable(
        route = MainNavigation.route(),
        deepLinks = MainNavigation.deepLinks,
    ) {
        val mainState = rememberMainState()
        MainScreen(mainState = mainState, navigateToSignIn = navigateToSignIn)
    }
}