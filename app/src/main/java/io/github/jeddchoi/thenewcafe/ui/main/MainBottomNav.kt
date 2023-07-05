package io.github.jeddchoi.thenewcafe.ui.main

import androidx.annotation.StringRes
import io.github.jeddchoi.designsystem.CafeIcons
import io.github.jeddchoi.designsystem.UiIcon
import io.github.jeddchoi.designsystem.UiText
import io.github.jeddchoi.thenewcafe.R

sealed class MainBottomNav(
    val route: String,
    @StringRes val titleId: Int,
    val selectedIcon: UiIcon,
    val unselectedIcon: UiIcon
) {
    object Profile : MainBottomNav(
        "profile",
        R.string.profile,
        UiIcon.ImageVectorIcon(CafeIcons.Profile_Filled, UiText.StringResource(R.string.desc_profile_selected)),
        UiIcon.ImageVectorIcon(CafeIcons.Profile, UiText.StringResource(R.string.desc_profile_unselected))
    )

    object Order : MainBottomNav(
        "order",
        R.string.order,
        UiIcon.ImageVectorIcon(CafeIcons.Order_Filled, UiText.StringResource(R.string.desc_order_selected)),
        UiIcon.ImageVectorIcon(CafeIcons.Order, UiText.StringResource(R.string.desc_order_unselected))
    )

    object MyPage : MainBottomNav(
        "mypage",
        R.string.mypage,
        UiIcon.ImageVectorIcon(CafeIcons.MyPage_Filled, UiText.StringResource(R.string.desc_mypage_selected)),
        UiIcon.ImageVectorIcon(CafeIcons.MyPage, UiText.StringResource(R.string.desc_mypage_unselected))
    )
}
