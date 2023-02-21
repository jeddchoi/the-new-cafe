package io.github.jeddchoi.mypage

import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import androidx.navigation.*
import androidx.navigation.compose.composable

const val myPageRoute = "mypage_route"
internal const val tabIdArg = "tabId"

internal class MyPageArgs(val tabId: String) {
    constructor(savedStateHandle: SavedStateHandle) :
            this(Uri.decode(checkNotNull(savedStateHandle[tabIdArg])))
}

fun NavController.navigateToMyPage(navOptions: NavOptions? = null) {
    this.navigate(myPageRoute, navOptions)
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
