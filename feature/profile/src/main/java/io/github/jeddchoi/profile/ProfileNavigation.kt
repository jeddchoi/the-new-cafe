package io.github.jeddchoi.profile

import androidx.annotation.StringRes
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.*
import androidx.navigation.compose.composable
import io.github.jeddchoi.designsystem.CafeIcons
import io.github.jeddchoi.designsystem.Icon
import io.github.jeddchoi.ui.feature.AppNavigation.Companion.baseAppUri
import io.github.jeddchoi.ui.feature.AppNavigation.Companion.baseWebUri
import io.github.jeddchoi.ui.feature.BottomNavigation
import io.github.jeddchoi.ui.model.UiState

/**
 * Profile navigation constants
 */
object ProfileNavigation : BottomNavigation {

    @StringRes
    override val titleTextId: Int = R.string.profile


    // routing
    override val name: String = "profile"
    override fun route(arg: String?): String = name

    override val arguments: List<NamedNavArgument> = listOf()
    override val deepLinks: List<NavDeepLink> = listOf(
        navDeepLink { uriPattern = "$baseWebUri/${route()}" },
        navDeepLink { uriPattern = "$baseAppUri/${route()}" }
    )

    // bottom navigation
    override val selectedIcon: Icon = Icon.ImageVectorIcon(CafeIcons.Profile_Filled)
    override val unselectedIcon: Icon = Icon.ImageVectorIcon(CafeIcons.Profile)

    @StringRes
    override val iconTextId: Int = R.string.profile
}

/**
 * Navigate to profile
 *
 * @param navOptions
 */
fun NavController.navigateToProfile(navOptions: NavOptions? = null) {
    this.navigate(ProfileNavigation.route(), navOptions)
}

/**
 * Bridge to Profile screen
 *
 * @param onNavigateToSignIn
 * @param onBackClick
 * @receiver
 * @receiver
 */
fun NavGraphBuilder.profileScreen(
    onNavigateToSignIn: () -> Unit,
    onBackClick: () -> Unit,
) {
    composable(
        route = ProfileNavigation.route(),
        deepLinks = ProfileNavigation.deepLinks
    ) {
        val viewModel: ProfileViewModel = hiltViewModel()
        val uiState by viewModel.uiState.collectAsStateWithLifecycle(UiState.InitialLoading)

        ProfileScreen(
            uiState = uiState,
            onNavigateToSignIn = onNavigateToSignIn,
            onSignOut = {
                viewModel.signOut()
                onNavigateToSignIn()
            },
            onBackClick = onBackClick,
        )
    }
}