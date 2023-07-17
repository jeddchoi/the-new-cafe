package io.github.jeddchoi.mypage

import android.content.Intent
import androidx.annotation.StringRes
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink


/**
 * Tabs which My page contains.
 * The order of these values will be order of tabs.
 *
 * @property titleId : string resource id of tab's title
 */
internal enum class MyPageTab(@StringRes val titleId: Int) {
    SESSION(R.string.session),
    HISTORY(R.string.history)
    ;
    companion object {
        val VALUES = values()
    }
}
internal const val MyPageTabArg = "tabId"
private const val MyPageRoutePattern = "mypage?$MyPageTabArg={${MyPageTabArg}}"

//internal class MyPageArgs(val tab: MyPageTab) {
//    constructor(savedStateHandle: SavedStateHandle) : this(
//        checkNotNull(
//            MyPageTab.valueOf(savedStateHandle.get<String>(MyPageTabArg)?.uppercase() ?: MyPageTab.SESSION.name)
//        )
//    )
//}

fun NavController.navigateToMyPage(navOptions: NavOptions? = null) {
    this.navigate(MyPageRoutePattern, navOptions)
}


@OptIn(ExperimentalAnimationApi::class)
fun NavGraphBuilder.myPageScreen(
    navigateToStoreList: () -> Unit = {},
    navigateToStore: (String) -> Unit = {},
    navigateToHistoryDetail: (String) -> Unit = {},
    navigateToSignIn: () -> Unit = {},
) {
    composable(
        route = MyPageRoutePattern,
        arguments = listOf(
            navArgument(MyPageTabArg) {
                type = NavType.StringType
                defaultValue = MyPageTab.SESSION.name
            }
        ),
        deepLinks = listOf(
            navDeepLink {
                uriPattern = "https://io.github.jeddchoi.thenewcafe/$MyPageRoutePattern"
                action = Intent.ACTION_VIEW
            },
            navDeepLink {
                uriPattern = "jeddchoi://thenewcafe/$MyPageRoutePattern"
                action = Intent.ACTION_VIEW
            }
        )
    ) { backStackEntry ->
        val viewModel: MyPageViewModel = hiltViewModel()
        val uiState by viewModel.uiState.collectAsStateWithLifecycle()
        val myPageTabArg = backStackEntry.arguments?.getString(MyPageTabArg)?.uppercase() ?: MyPageTab.SESSION.name

        var selectedTab by rememberSaveable(myPageTabArg) {
            mutableStateOf(MyPageTab.valueOf(myPageTabArg))
        }
        MyPageScreen(
            selectedTab = selectedTab,
            uiState = uiState,
            selectTab = {
                selectedTab = it
            },
            sendRequest = viewModel::sendRequest,
            navigateToHistoryDetail = navigateToHistoryDetail,
            navigateToSignIn = navigateToSignIn
        )
    }
}
