package io.github.jeddchoi.mypage

import android.net.Uri
import android.util.Log
import androidx.annotation.StringRes
import androidx.navigation.*
import androidx.navigation.compose.composable
import io.github.jeddchoi.designsystem.CafeIcons
import io.github.jeddchoi.designsystem.Icon
import io.github.jeddchoi.mypage.MyPageNavigation.tabIdArg
import io.github.jeddchoi.ui.LogCompositions
import io.github.jeddchoi.ui.feature.AppNavigation
import io.github.jeddchoi.ui.feature.baseAppUri
import io.github.jeddchoi.ui.feature.baseWebUri
import java.util.*

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

object MyPageNavigation : AppNavigation {
    override val name: String = "mypage"

    override val selectedIcon: Icon = Icon.ImageVectorIcon(CafeIcons.MyPage_Filled)
    override val unselectedIcon: Icon = Icon.ImageVectorIcon(CafeIcons.MyPage)

    @StringRes
    override val iconTextId: Int = R.string.mypage

    @StringRes
    override val titleTextId: Int = R.string.mypage

    const val tabIdArg = "tabId"
    override fun route(arg: String?): String = "$name?$tabIdArg=${arg ?: "{$tabIdArg}"}"

    override val arguments: List<NamedNavArgument> = listOf(
        navArgument(tabIdArg) {
            type = NavType.StringType
            nullable = true
        }
    )
    override val deepLinks: List<NavDeepLink> = listOf(
        navDeepLink { uriPattern = "$baseWebUri/${route()}" },
        navDeepLink { uriPattern = "$baseAppUri/${route()}" }
    )

    

    val myPageTabs = MyPageTab.values()
}


fun NavController.navigateToMyPage(
    navOptions: NavOptions? = null,
    tab: MyPageTab? = null,
) {
    Log.i("TAG", "Navigate to MyPage $tab")
    this.navigate(MyPageNavigation.route(tab?.name), navOptions)
}

fun NavGraphBuilder.myPageScreen(
    shouldHandleReselection: Boolean,
    onHandleReselection: () -> Unit,
    onBackClick: () -> Unit = {},
) {
    composable(
        route = MyPageNavigation.route(),
        deepLinks = MyPageNavigation.deepLinks,
        arguments = MyPageNavigation.arguments,
    ) { backStackEntry ->


        val navArg = backStackEntry.arguments?.getString(tabIdArg)?.uppercase(Locale.getDefault())?.also {
            backStackEntry.arguments?.putString(tabIdArg, "")
        }
        val navTab = try {
            navArg?.let { MyPageTab.valueOf(it) }
        } catch (e: IllegalArgumentException) {
            null
        }
        LogCompositions(tag = "TAG", msg = "MyPage : backStackEntry = ${backStackEntry.arguments?.getString(tabIdArg)}\n navArg = $navArg / navTab = $navTab\n savedHandle = ${Uri.decode(backStackEntry.savedStateHandle[tabIdArg])}")

        MyPageRoute(
            navTab = navTab,
            shouldHandleReselection = shouldHandleReselection,
            onHandleReselection = onHandleReselection,
            onBackClick = onBackClick
        )
    }
}
