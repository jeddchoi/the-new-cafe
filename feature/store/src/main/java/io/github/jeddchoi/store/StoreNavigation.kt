package io.github.jeddchoi.store

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument

const val storeRoute = "store_route"
internal const val storeIdArg = "storeId"

//internal class StoreArgs(val storeId: String) {
//    constructor(savedStateHandle: SavedStateHandle) :
//            this(Uri.decode(checkNotNull(savedStateHandle[storeId])))
//}


fun NavGraphBuilder.storeScreen(
    onBackClick: () -> Unit,
) {
    composable(
        route = "${storeRoute}/{$storeIdArg}",
        arguments = listOf(
            navArgument(storeIdArg) { type = NavType.StringType },
        ),
    ) {
        StoreRoute(onBackClick = onBackClick)
    }
}