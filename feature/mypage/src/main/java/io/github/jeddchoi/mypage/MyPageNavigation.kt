package io.github.jeddchoi.mypage

import androidx.annotation.StringRes
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.*
import androidx.navigation.compose.composable
import java.util.*


/**
 * Tabs which My page contains.
 * The order of these values will be order of tabs.
 *
 * @property titleId : string resource id of tab's title
 */
internal enum class MyPageTab(@StringRes val titleId: Int) {
    Session(R.string.session),
    History(R.string.history)
    ;
    companion object {
        val VALUES = values()
    }
}
private const val MyPageTabArg = "tabId"
private const val MyPageRoutePattern = "mypage?$MyPageTabArg={${MyPageTabArg}}"

internal class MyPageArgs(val tab: MyPageTab) {
    constructor(savedStateHandle: SavedStateHandle) : this(
        checkNotNull(
            MyPageTab.valueOf(savedStateHandle[MyPageTabArg] ?: MyPageTab.Session.name)
        )
    )
}

fun NavController.navigateToMyPage(navOptions: NavOptions? = null) {
    this.navigate(MyPageRoutePattern, navOptions)
}


@OptIn(ExperimentalFoundationApi::class)
fun NavGraphBuilder.myPageScreen(
    onNavigateToSignIn: () -> Unit,
    navigateToStoreList: () -> Unit,
    navigateToStore: (String) -> Unit,
    onBackClick: () -> Unit,
) {
    composable(
        route = MyPageRoutePattern,
        arguments = listOf(
            navArgument(MyPageTabArg) {
                type = NavType.StringType
                defaultValue = MyPageTab.Session.name
            }
        )
    ) { backStackEntry ->
        val viewModel: MyPageViewModel = hiltViewModel()
        val uiState by viewModel.uiState.collectAsStateWithLifecycle()

        MyPageScreen(
            navigateTab = MyPageArgs(backStackEntry.savedStateHandle).tab,
            uiState = uiState
        )
    }
}
