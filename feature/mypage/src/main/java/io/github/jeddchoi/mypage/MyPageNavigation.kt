package io.github.jeddchoi.mypage

import android.util.Log
import androidx.annotation.StringRes
import androidx.navigation.*
import androidx.navigation.compose.composable
import io.github.jeddchoi.ui.LogCompositions
import java.util.*

const val tabIdArg = "tabId"
const val myPageRoute = "mypage"
const val myPageRouteWithTabId = "$myPageRoute?$tabIdArg={$tabIdArg}"


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


fun NavController.navigateToMyPage(
    navOptions: NavOptions? = null,
    tab: MyPageTab? = null,
) {
    Log.i("TAG", "Navigate to MyPage $tab")
    if (tab == null) this.navigate(myPageRoute, navOptions)
    else this.navigate("$myPageRoute?$tabIdArg=${tab.name}", navOptions)
}

fun NavGraphBuilder.myPageScreen(
    baseWebUri: String,
    baseAppUri: String,
    shouldHandleReselection: Boolean,
    onHandleReselection: () -> Unit,
    onBackClick: () -> Unit = {},
) {
    composable(
        route = myPageRouteWithTabId,
        deepLinks = listOf(
            navDeepLink { uriPattern = "$baseWebUri/$myPageRoute/{$tabIdArg}" },
            navDeepLink { uriPattern = "$baseAppUri/$myPageRoute/{$tabIdArg}" }
        ),
        arguments = listOf(
            navArgument(tabIdArg) {
                type = NavType.StringType
                nullable = true
            },
        ),
    ) { backStackEntry ->

        LogCompositions(tag = "TAG", msg = "MyPage : backStackEntry = ${backStackEntry.arguments}")
        val navArg = backStackEntry.arguments?.getString(tabIdArg)?.uppercase(Locale.getDefault())
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
