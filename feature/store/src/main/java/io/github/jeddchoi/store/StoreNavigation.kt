package io.github.jeddchoi.store

import android.net.Uri
import androidx.annotation.StringRes
import androidx.lifecycle.SavedStateHandle
import androidx.navigation.*
import androidx.navigation.compose.composable
import io.github.jeddchoi.designsystem.CafeIcons
import io.github.jeddchoi.designsystem.Icon
import io.github.jeddchoi.store.StoreNavigation.storeIdArg
import io.github.jeddchoi.ui.feature.AppNavigation
import io.github.jeddchoi.ui.feature.baseAppUri
import io.github.jeddchoi.ui.feature.baseWebUri


object StoreNavigation : AppNavigation {
    override val name: String = "store"
    override val selectedIcon: Icon = Icon.ImageVectorIcon(CafeIcons.Store_Filled)
    override val unselectedIcon: Icon = Icon.ImageVectorIcon(CafeIcons.Store)

    @StringRes
    override val iconTextId: Int = R.string.store
    @StringRes
    override val titleTextId: Int = R.string.store

    override fun route(arg: String?): String = "$name/${arg ?: "{$storeIdArg}"}"
    const val storeIdArg = "storeId"

    override val arguments: List<NamedNavArgument> = listOf(
        navArgument(storeIdArg) { type = NavType.StringType },
    )
    override val deepLinks: List<NavDeepLink> = listOf(
        navDeepLink { uriPattern = "$baseWebUri/${route()}" },
        navDeepLink { uriPattern = "$baseAppUri/${route()}" }
    )
}
internal class StoreArgs(val storeId: String) {
    constructor(savedStateHandle: SavedStateHandle) : this(Uri.decode(checkNotNull(savedStateHandle[storeIdArg])))
}

fun NavController.navigateToStore(storeId: String) {
    val encodedId = Uri.encode(storeId)
    this.navigate(StoreNavigation.route(encodedId))
}
fun NavGraphBuilder.storeScreen(
    onBackClick: () -> Unit = {},
) {
    composable(
        route = StoreNavigation.route(),
        deepLinks = StoreNavigation.deepLinks,
        arguments = StoreNavigation.arguments,
    ) {
        StoreRoute(onBackClick = onBackClick)
    }
}