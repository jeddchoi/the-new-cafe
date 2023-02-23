package io.github.jeddchoi.store

import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import androidx.navigation.*
import androidx.navigation.compose.composable

const val storeRoute = "store"
internal const val storeIdArg = "storeId"

internal class StoreArgs(val storeId: String) {
    constructor(savedStateHandle: SavedStateHandle) : this(Uri.decode(checkNotNull(savedStateHandle[storeIdArg])))
}

fun NavController.navigateToStore(storeId: String) {
    val encodedId = Uri.encode(storeId)
    this.navigate("$storeRoute/$encodedId")
}
fun NavGraphBuilder.storeScreen(
    baseWebUri: String,
    baseAppUri: String,
    onBackClick: () -> Unit = {},
) {
    composable(
        route = "${storeRoute}/{$storeIdArg}",
        deepLinks = listOf(
            navDeepLink { uriPattern = "$baseWebUri/$storeRoute/{$storeIdArg}" },
            navDeepLink { uriPattern = "$baseAppUri/$storeRoute/{$storeIdArg}" }
        ),
        arguments = listOf(
            navArgument(storeIdArg) { type = NavType.StringType },
        ),
    ) {
        StoreRoute(onBackClick = onBackClick)
    }
}