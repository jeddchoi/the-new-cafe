package io.github.jeddchoi.thenewcafe.ui.main

import androidx.annotation.StringRes
import io.github.jeddchoi.common.CafeIcons
import io.github.jeddchoi.common.UiIcon
import io.github.jeddchoi.common.UiText
import io.github.jeddchoi.thenewcafe.R

enum class MainBottomNav(
    val route: String,
    @StringRes val titleId: Int,
    val selectedIcon: UiIcon,
    val unselectedIcon: UiIcon
) {
    Profile(
        "profile",
        R.string.profile,
        UiIcon.ImageVectorIcon(
            CafeIcons.Profile_Filled,
            UiText.StringResource(R.string.desc_profile_selected)
        ),
        UiIcon.ImageVectorIcon(
            CafeIcons.Profile,
            UiText.StringResource(R.string.desc_profile_unselected)
        )
    ),

    Order(
        "order",
        R.string.order,
        UiIcon.ImageVectorIcon(
            CafeIcons.Order_Filled,
            UiText.StringResource(R.string.desc_order_selected)
        ),
        UiIcon.ImageVectorIcon(
            CafeIcons.Order,
            UiText.StringResource(R.string.desc_order_unselected)
        )
    ),

    MyPage(
        "mypage",
        R.string.mypage,
        UiIcon.ImageVectorIcon(
            CafeIcons.MyPage_Filled,
            UiText.StringResource(R.string.desc_mypage_selected)
        ),
        UiIcon.ImageVectorIcon(
            CafeIcons.MyPage,
            UiText.StringResource(R.string.desc_mypage_unselected)
        )
    );

    companion object {
        val VALUES = values()
    }
}
