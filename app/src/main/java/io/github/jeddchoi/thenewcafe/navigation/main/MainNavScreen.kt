package io.github.jeddchoi.thenewcafe.navigation.main

import androidx.annotation.StringRes
import io.github.jeddchoi.designsystem.CafeIcons
import io.github.jeddchoi.designsystem.Icon
import io.github.jeddchoi.thenewcafe.R

sealed class MainNavScreen(val route: String, @StringRes val titleId: Int, val selectedIcon: Icon, val unselectedIcon: Icon) {
    object Profile: MainNavScreen("profile",R.string.profile, Icon.ImageVectorIcon(CafeIcons.Profile_Filled), Icon.ImageVectorIcon(CafeIcons.Profile))
    object Order: MainNavScreen("order", R.string.order, Icon.ImageVectorIcon(CafeIcons.Order_Filled), Icon.ImageVectorIcon(CafeIcons.Order))
    object MyPage: MainNavScreen("mypage", R.string.mypage, Icon.ImageVectorIcon(CafeIcons.MyPage_Filled), Icon.ImageVectorIcon(CafeIcons.MyPage))
}
