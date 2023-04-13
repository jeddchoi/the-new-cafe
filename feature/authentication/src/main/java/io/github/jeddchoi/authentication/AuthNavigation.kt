package io.github.jeddchoi.authentication

import androidx.annotation.StringRes
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.*
import androidx.navigation.compose.composable
import io.github.jeddchoi.ui.feature.AppNavigation
import io.github.jeddchoi.ui.feature.AppNavigation.Companion.baseAppUri
import io.github.jeddchoi.ui.feature.AppNavigation.Companion.baseWebUri
import io.github.jeddchoi.ui.feature.GraphStartNavigation

object SignInNavigation : AppNavigation, GraphStartNavigation {
    @StringRes
    override val titleTextId: Int = R.string.sign_in

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

object RegisterNavigation : AppNavigation {
    @StringRes
    override val titleTextId: Int = R.string.register

    // routing
    override val name: String = "register"
    override fun route(arg: String?): String = name
    override val arguments: List<NamedNavArgument> = listOf()
    override val deepLinks: List<NavDeepLink> = listOf(
        navDeepLink { uriPattern = "$baseWebUri/${route()}" },
        navDeepLink { uriPattern = "$baseAppUri/${route()}" },
    )
}


fun NavController.navigateToAuth(navOptions: NavOptions? = null) {
    this.navigate(SignInNavigation.routeGraph, navOptions)
}

fun NavController.navigateToRegister(navOptions: NavOptions? = null) {
    this.navigate(RegisterNavigation.route(), navOptions)
}

fun NavGraphBuilder.authGraph(
    navController: NavHostController,
    onBackClick: () -> Unit,
) {
    navigation(
        route = SignInNavigation.routeGraph,
        startDestination = SignInNavigation.route(),
    ) {
        composable(
            route = SignInNavigation.route(),
            deepLinks = SignInNavigation.deepLinks
        ) {backStackEntry ->
            val authEntry = remember(backStackEntry) {
                navController.getBackStackEntry(SignInNavigation.routeGraph)
            }
            val authViewModel = hiltViewModel<AuthViewModel>(authEntry)
            SignInScreen(
                viewModel = authViewModel,
                onBackClick = onBackClick,
                navigateToRegisterClick = {
                    navController.navigateToRegister(navOptions {
                        popUpTo(SignInNavigation.route())
                        launchSingleTop = true
                    })
                }
            )
        }

        composable(
            route = RegisterNavigation.route(),
            deepLinks = RegisterNavigation.deepLinks
        ) {backStackEntry ->
            val authEntry = remember(backStackEntry) {
                navController.getBackStackEntry(SignInNavigation.routeGraph)
            }
            val authViewModel = hiltViewModel<AuthViewModel>(authEntry)
            RegisterScreen(
                viewModel = authViewModel,
                onBackClick = onBackClick,
                navigateToSignInClick = {
                    navController.navigateToAuth(navOptions {
                        popUpTo(SignInNavigation.routeGraph) {
                            inclusive = true
                        }
                        launchSingleTop = true
                    })
                }
            )
        }

    }
}
