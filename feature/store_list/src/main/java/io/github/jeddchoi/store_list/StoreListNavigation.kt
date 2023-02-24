package io.github.jeddchoi.store_list

import android.util.Log
import androidx.annotation.StringRes
import androidx.navigation.*
import androidx.navigation.compose.composable
import io.github.jeddchoi.designsystem.CafeIcons
import io.github.jeddchoi.designsystem.Icon
import io.github.jeddchoi.ui.feature.AppNavigation
import io.github.jeddchoi.ui.feature.baseAppUri
import io.github.jeddchoi.ui.feature.baseWebUri



object StoreListNavigation : AppNavigation {
    override val name: String = "stores"

    override val selectedIcon: Icon = Icon.ImageVectorIcon(CafeIcons.Order_Filled)
    override val unselectedIcon: Icon = Icon.ImageVectorIcon(CafeIcons.Order)

    @StringRes
    override val iconTextId: Int = R.string.order
    @StringRes
    override val titleTextId: Int = R.string.order

    override fun route(arg: String?): String = name
    val routeGraph = "order"

    override val arguments: List<NamedNavArgument> = listOf()
    override val deepLinks: List<NavDeepLink> = listOf(
        navDeepLink { uriPattern = "$baseWebUri/${route()}" },
        navDeepLink { uriPattern = "$baseAppUri/${route()}" },
        navDeepLink { uriPattern = "$baseWebUri/${routeGraph}" },
        navDeepLink { uriPattern = "$baseAppUri/${routeGraph}" }
    )
}


fun NavController.navigateToOrderGraph(navOptions: NavOptions? = null) {
    Log.i("TAG", "Navigate to Order")
    this.navigate(StoreListNavigation.routeGraph, navOptions)
}

fun NavGraphBuilder.orderGraph(
    navigateToStore: (String) -> Unit,
    nestedGraphs: NavGraphBuilder.() -> Unit,
) {
    navigation(
        route = StoreListNavigation.routeGraph,
        startDestination = StoreListNavigation.route(),
    ) {
        composable(
            route = StoreListNavigation.route(),
            deepLinks = StoreListNavigation.deepLinks
        ) {backStackEntry ->
//            LogCompositions(tag = "TAG", msg = "stores : backStackEntry = ${backStackEntry.arguments}")
            StoreListRoute(
                navigateToSeats = navigateToStore,
            )
        }
        nestedGraphs()
    }
}
