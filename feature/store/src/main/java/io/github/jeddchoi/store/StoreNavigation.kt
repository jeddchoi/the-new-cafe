package io.github.jeddchoi.store

import android.net.Uri
import androidx.annotation.StringRes
import androidx.compose.runtime.getValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.*
import androidx.navigation.compose.composable
import io.github.jeddchoi.designsystem.CafeIcons
import io.github.jeddchoi.designsystem.Icon
import io.github.jeddchoi.store.StoreNavigation.storeIdArg
import io.github.jeddchoi.ui.feature.BottomNavigation
import io.github.jeddchoi.ui.feature.baseAppUri
import io.github.jeddchoi.ui.feature.baseWebUri


object StoreNavigation : BottomNavigation {
    override val name: String = "stores"
    override val selectedIcon: Icon = Icon.ImageVectorIcon(CafeIcons.Store_Filled)
    override val unselectedIcon: Icon = Icon.ImageVectorIcon(CafeIcons.Store)

    @StringRes
    override val iconTextId: Int = R.string.store

    @StringRes
    override val titleTextId: Int = R.string.store

    override fun route(arg: String?): String = "$name/${arg ?: "{$storeIdArg}"}"
    const val storeIdArg = "storeId"

    override val arguments: List<NamedNavArgument> = listOf(
        navArgument(storeIdArg) {
            type = NavType.StringType
            nullable = false
        },
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
        val viewModel: StoreViewModel = viewModel()
        val uiState by viewModel.uiState.collectAsStateWithLifecycle()
        StoreScreen(uiState = uiState)
    }
}