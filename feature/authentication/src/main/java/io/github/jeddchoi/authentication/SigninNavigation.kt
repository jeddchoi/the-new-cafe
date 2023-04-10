package io.github.jeddchoi.authentication

import androidx.annotation.StringRes
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.*
import androidx.navigation.compose.composable
import io.github.jeddchoi.ui.feature.AppNavigation
import io.github.jeddchoi.ui.feature.GraphStartNavigation
import io.github.jeddchoi.ui.feature.baseAppUri
import io.github.jeddchoi.ui.feature.baseWebUri

object SigninNavigation : AppNavigation, GraphStartNavigation {
    @StringRes
    override val titleTextId: Int = R.string.auth

    // routing
    override val name: String = "signin"
    override val routeGraph = "auth"
    override fun route(arg: String?): String = name
    override val arguments: List<NamedNavArgument> = listOf()
    override val deepLinks: List<NavDeepLink> = listOf(
        navDeepLink { uriPattern = "$baseWebUri/${route()}" },
        navDeepLink { uriPattern = "$baseAppUri/${route()}" },
        navDeepLink { uriPattern = "$baseWebUri/${routeGraph}" },
        navDeepLink { uriPattern = "$baseAppUri/${routeGraph}" }
    )
}


fun NavController.navigateToAuth(navOptions: NavOptions? = null) {
    this.navigate(SigninNavigation.routeGraph, navOptions)
}

fun NavGraphBuilder.authGraph(
    nestedGraphs: NavGraphBuilder.() -> Unit,
    onBackClick: () -> Unit,
) {
    navigation(
        route = SigninNavigation.routeGraph,
        startDestination = SigninNavigation.route(),
    ) {
        composable(
            route = SigninNavigation.route(),
            deepLinks = SigninNavigation.deepLinks
        ) {
            val viewModel: AuthViewModel = viewModel()
//            val uiState by viewModel.uiState.collectAsStateWithLifecycle()


        }
        nestedGraphs()
    }
}
