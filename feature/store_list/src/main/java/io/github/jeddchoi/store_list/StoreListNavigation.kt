package io.github.jeddchoi.store_list

import androidx.annotation.StringRes
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.*
import androidx.navigation.compose.composable
import io.github.jeddchoi.designsystem.CafeIcons
import io.github.jeddchoi.designsystem.Icon
import io.github.jeddchoi.ui.feature.AppNavigation.Companion.baseAppUri
import io.github.jeddchoi.ui.feature.AppNavigation.Companion.baseWebUri
import io.github.jeddchoi.ui.feature.BottomNavigation
import io.github.jeddchoi.ui.feature.GraphStartNavigation


object StoreListNavigation : BottomNavigation, GraphStartNavigation {
    @StringRes
    override val titleTextId: Int = R.string.order

    // routing
    override val name: String = "stores"
    override val routeGraph = "order"
    override fun route(arg: String?): String = name
    override val arguments: List<NamedNavArgument> = listOf()
    override val deepLinks: List<NavDeepLink> = listOf(
        navDeepLink { uriPattern = "$baseWebUri/${route()}" },
        navDeepLink { uriPattern = "$baseAppUri/${route()}" },
        navDeepLink { uriPattern = "$baseWebUri/${routeGraph}" },
        navDeepLink { uriPattern = "$baseAppUri/${routeGraph}" }
    )

    // bottom navigation
    override val selectedIcon: Icon = Icon.ImageVectorIcon(CafeIcons.Order_Filled)
    override val unselectedIcon: Icon = Icon.ImageVectorIcon(CafeIcons.Order)
    @StringRes
    override val iconTextId: Int = R.string.order

}


fun NavController.navigateToOrder(navOptions: NavOptions? = null) {
    this.navigate(StoreListNavigation.routeGraph, navOptions)
}

fun NavGraphBuilder.orderGraph(
    onNavigateToSignIn: () -> Unit,
    navigateToStore: (String) -> Unit,
    navigateToMyStatus: () -> Unit,
    nestedGraphs: NavGraphBuilder.() -> Unit,
    onBackClick: () -> Unit,
) {
    navigation(
        route = StoreListNavigation.routeGraph,
        startDestination = StoreListNavigation.route(),
    ) {
        composable(
            route = StoreListNavigation.route(),
            deepLinks = StoreListNavigation.deepLinks
        ) {
            val viewModel: StoreListViewModel = viewModel()
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()

            StoreListScreen(uiState = uiState)
        }
        nestedGraphs()
    }
}
