package io.github.jeddchoi.order

import android.util.Log
import androidx.annotation.StringRes
import androidx.navigation.*
import androidx.navigation.compose.composable
import io.github.jeddchoi.designsystem.CafeIcons
import io.github.jeddchoi.designsystem.Icon
import io.github.jeddchoi.ui.LogCompositions
import io.github.jeddchoi.ui.feature.AppNavigation
import io.github.jeddchoi.ui.feature.baseAppUri
import io.github.jeddchoi.ui.feature.baseWebUri



object OrderNavigation : AppNavigation {
    override val name: String = "order"

    override val selectedIcon: Icon = Icon.ImageVectorIcon(CafeIcons.Order_Filled)
    override val unselectedIcon: Icon = Icon.ImageVectorIcon(CafeIcons.Order)

    @StringRes
    override val iconTextId: Int = R.string.order
    @StringRes
    override val titleTextId: Int = R.string.order

    override fun route(arg: String?): String = name
    val routeGraph = "order_graph"

    override val arguments: List<NamedNavArgument> = listOf()
    override val deepLinks: List<NavDeepLink> = listOf(
        navDeepLink { uriPattern = "$baseWebUri/${route()}" },
        navDeepLink { uriPattern = "$baseAppUri/${route()}" }
    )
}


fun NavController.navigateToOrderGraph(navOptions: NavOptions? = null) {
    Log.i("TAG", "Navigate to Order")
    this.navigate(OrderNavigation.routeGraph, navOptions)
}

fun NavGraphBuilder.orderGraph(
    navigateToStore: (String) -> Unit,
    nestedGraphs: NavGraphBuilder.() -> Unit,
) {
    navigation(
        route = OrderNavigation.routeGraph,
        startDestination = OrderNavigation.route(),
    ) {
        composable(
            route = OrderNavigation.route(),
            deepLinks = OrderNavigation.deepLinks
        ) {backStackEntry ->
            LogCompositions(tag = "TAG", msg = "Order : backStackEntry = ${backStackEntry.arguments}")
            OrderRoute(
                navigateToSeats = navigateToStore,
            )
        }
        nestedGraphs()
    }
}
