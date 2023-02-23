package io.github.jeddchoi.mypage

import androidx.annotation.StringRes
import androidx.navigation.*
import androidx.navigation.compose.composable

internal const val tabIdArg = "tabId"
const val myPageRoute = "mypage_route"
const val myPageRouteWithTabId = "$myPageRoute?$tabIdArg={$tabIdArg}"

val uri = "https://www.example.com"
val appUri = "jeddchoi://thenewcafe"
/**
 * Tabs which My page contains.
 * The order of these values will be order of tabs.
 * Conditional navigation argument will be translated to this enum value
 * by its name(e.g. MY_STATUS)
 *
 * @property titleId : string resource id of tab's title
 */
enum class MyPageTab(@StringRes val titleId: Int) {
    MY_STATUS(R.string.my_status),
    ACTION_LOG(R.string.action_log)
}

val myPageTabs = MyPageTab.values()

//internal class MyPageArgs(val selectedTabId: String) {
//    constructor(savedStateHandle: SavedStateHandle) : this(
//        Uri.decode(checkNotNull(savedStateHandle[tabIdArg]))
//    )
//}


fun NavController.navigateToMyPage(
    navOptions: NavOptions? = null,
    tab: MyPageTab? = null,
) {
    if (tab == null) this.navigate(myPageRoute, navOptions)
    else this.navigate("$myPageRoute?$tabIdArg=${tab.name}", navOptions)
}

fun NavGraphBuilder.myPageScreen(
    shouldHandleReselection: Boolean,
    onHandleReselection: () -> Unit,
    onBackClick: () -> Unit = {},
) {
    composable(
        route = myPageRouteWithTabId,
        deepLinks = listOf(
            navDeepLink { uriPattern = "$uri/{$tabIdArg}" },
            navDeepLink { uriPattern = "$appUri/{$tabIdArg}" }
        ),
        arguments = listOf(
            navArgument(tabIdArg) {
                type = NavType.StringType
                nullable = true
            },
        ),
    ) { backStackEntry ->

        val navArg = backStackEntry.arguments?.getString(tabIdArg)
        val navTab = try {
            navArg?.let { MyPageTab.valueOf(it) }
        } catch (e: IllegalArgumentException) {
            null
        }


        MyPageRoute(
            navTab = navTab,
            shouldHandleReselection = shouldHandleReselection,
            onHandleReselection = onHandleReselection,
            onBackClick = onBackClick
        )
    }
}
