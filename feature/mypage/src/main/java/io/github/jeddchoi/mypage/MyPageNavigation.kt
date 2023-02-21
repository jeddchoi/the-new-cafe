package io.github.jeddchoi.mypage

import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument

const val myPageRoute = "mypage_route"
internal const val tabIdArg = "tabId"

internal class MyPageArgs(val tabId: String) {
    constructor(savedStateHandle: SavedStateHandle) :
            this(Uri.decode(checkNotNull(savedStateHandle[tabIdArg])))
}


fun NavGraphBuilder.myPageScreen(
    onBackClick: () -> Unit = {},
) {
    composable(
        route = "$myPageRoute?$tabIdArg={$tabIdArg}",
        arguments = listOf(
            navArgument(tabIdArg) {
                type = NavType.StringType
                defaultValue = "my_status"
            },
        ),
    ) {
        MyPageRoute(onBackClick = onBackClick)
    }
}
