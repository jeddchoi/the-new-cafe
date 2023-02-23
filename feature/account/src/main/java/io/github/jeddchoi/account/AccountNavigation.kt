package io.github.jeddchoi.account

import android.util.Log
import androidx.annotation.StringRes
import androidx.navigation.*
import androidx.navigation.compose.composable
import io.github.jeddchoi.designsystem.CafeIcons
import io.github.jeddchoi.designsystem.Icon
import io.github.jeddchoi.ui.feature.AppNavigation
import io.github.jeddchoi.ui.feature.baseAppUri
import io.github.jeddchoi.ui.feature.baseWebUri

object AccountNavigation : AppNavigation {
    override val name: String = "account"

    override val selectedIcon: Icon = Icon.ImageVectorIcon(CafeIcons.Account_Filled)
    override val unselectedIcon: Icon = Icon.ImageVectorIcon(CafeIcons.Account)

    @StringRes
    override val iconTextId: Int = R.string.account
    @StringRes
    override val titleTextId: Int = R.string.account

    override fun route(arg: String?): String = name

    override val arguments: List<NamedNavArgument> = listOf()
    override val deepLinks: List<NavDeepLink> = listOf(
        navDeepLink { uriPattern = "$baseWebUri/${route()}" },
        navDeepLink { uriPattern = "$baseAppUri/${route()}" }
    )
}


fun NavController.navigateToAccount(navOptions: NavOptions? = null) {
    Log.i("TAG", "Navigate to Account")
    this.navigate(AccountNavigation.route(), navOptions)
}


fun NavGraphBuilder.accountScreen(
    onShowActionLog: () -> Unit = {},
) {

    composable(
        route = AccountNavigation.route(),
        deepLinks = AccountNavigation.deepLinks
    ) { _ ->
        AccountRoute(onShowActionLog = onShowActionLog)
    }
}
