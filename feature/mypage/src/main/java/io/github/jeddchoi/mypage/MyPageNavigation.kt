package io.github.jeddchoi.mypage

import androidx.annotation.StringRes
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.*
import androidx.navigation.compose.composable
import io.github.jeddchoi.designsystem.CafeIcons
import io.github.jeddchoi.designsystem.Icon
import io.github.jeddchoi.ui.feature.AppNavigation.Companion.baseAppUri
import io.github.jeddchoi.ui.feature.AppNavigation.Companion.baseWebUri
import io.github.jeddchoi.ui.feature.BottomNavigation
import java.util.*


object MyPageNavigation : BottomNavigation {
    @StringRes
    override val titleTextId: Int = R.string.mypage


    // routing
    override val name: String = "mypage"
    override fun route(arg: String?): String = "$name/{$tabIdArg}"

    const val tabIdArg = "tabId"

    class Args(val tab: Tab) {
        constructor(savedStateHandle: SavedStateHandle) : this(
            checkNotNull(
                Tab.valueOf(
                    savedStateHandle[tabIdArg] ?: Tab.MY_STATUS.name
                )
            )
        )
    }

    override val arguments: List<NamedNavArgument> = listOf(
        navArgument(tabIdArg) { type = NavType.StringType }
    )
    override val deepLinks: List<NavDeepLink> = listOf(
        navDeepLink { uriPattern = "$baseWebUri/${route()}" },
        navDeepLink { uriPattern = "$baseAppUri/${route()}" }
    )

    // bottom navigation
    override val selectedIcon: Icon = Icon.ImageVectorIcon(CafeIcons.MyPage_Filled)
    override val unselectedIcon: Icon = Icon.ImageVectorIcon(CafeIcons.MyPage)

    @StringRes
    override val iconTextId: Int = R.string.mypage


    // tabs
    val tabs = Tab.values()

    /**
     * Tabs which My page contains.
     * The order of these values will be order of tabs.
     *
     * @property titleId : string resource id of tab's title
     */
    enum class Tab(@StringRes val titleId: Int) {
        MY_STATUS(R.string.my_status),
        ACTION_LOG(R.string.action_log)
    }
}


fun NavController.navigateToMyPage(navOptions: NavOptions? = null) {
    this.navigate(MyPageNavigation.route(MyPageNavigation.Tab.MY_STATUS.name), navOptions)
}

fun NavController.navigateToMyPageActionLog(navOptions: NavOptions? = null) {
    this.navigate(MyPageNavigation.route(MyPageNavigation.Tab.ACTION_LOG.name), navOptions)
}



@OptIn(ExperimentalFoundationApi::class)
fun NavGraphBuilder.myPageScreen(
    onNavigateToSignIn: () -> Unit,
    navigateToStoreList: () -> Unit,
    navigateToStore: (String) -> Unit,
    onBackClick: () -> Unit,
) {
    composable(
        route = MyPageNavigation.route(),
        deepLinks = MyPageNavigation.deepLinks,
        arguments = MyPageNavigation.arguments,
    ) { backStackEntry ->
        val viewModel: MyPageViewModel = hiltViewModel()
        val uiState by viewModel.uiState.collectAsStateWithLifecycle()

        MyPageScreen(
            navigateTab = MyPageNavigation.Args(backStackEntry.savedStateHandle).tab,
            uiState = uiState
        )
    }
}
