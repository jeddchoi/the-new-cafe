package io.github.jeddchoi.account

import androidx.annotation.StringRes
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.*
import androidx.navigation.compose.composable
import io.github.jeddchoi.designsystem.CafeIcons
import io.github.jeddchoi.designsystem.Icon
import io.github.jeddchoi.ui.feature.BottomNavigation
import io.github.jeddchoi.ui.feature.UiState
import io.github.jeddchoi.ui.feature.baseAppUri
import io.github.jeddchoi.ui.feature.baseWebUri

/**
 * Account navigation constants
 */
object AccountNavigation : BottomNavigation {

    @StringRes
    override val titleTextId: Int = R.string.account


    // routing
    override val name: String = "account"
    override fun route(arg: String?): String = name

    override val arguments: List<NamedNavArgument> = listOf()
    override val deepLinks: List<NavDeepLink> = listOf(
        navDeepLink { uriPattern = "$baseWebUri/${route()}" },
        navDeepLink { uriPattern = "$baseAppUri/${route()}" }
    )

    // bottom navigation
    override val selectedIcon: Icon = Icon.ImageVectorIcon(CafeIcons.Account_Filled)
    override val unselectedIcon: Icon = Icon.ImageVectorIcon(CafeIcons.Account)
    @StringRes
    override val iconTextId: Int = R.string.account
}

/**
 * Navigate to account
 *
 * @param navOptions
 */
fun NavController.navigateToAccount(navOptions: NavOptions? = null) {
    this.navigate(AccountNavigation.route(), navOptions)
}

/**
 * Bridge to Account screen
 *
 * @param onNavigateToSignIn
 * @param onBackClick
 * @receiver
 * @receiver
 */
fun NavGraphBuilder.accountScreen(
    onNavigateToSignIn: () -> Unit,
    onBackClick: () -> Unit,
) {
    composable(
        route = AccountNavigation.route(),
        deepLinks = AccountNavigation.deepLinks
    ) {
        val viewModel: AccountViewModel = viewModel()
        val uiState by viewModel.uiState.collectAsStateWithLifecycle(UiState.Loading())

        AccountScreen(
            uiState = uiState,
            onNavigateToSignIn = onNavigateToSignIn,
            onSignOut = { viewModel.signOut() },
            onBackClick = onBackClick,
        )
    }
}