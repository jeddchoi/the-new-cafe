package io.github.jeddchoi.thenewcafe.navigation

import io.github.jeddchoi.designsystem.CafeIcons
import io.github.jeddchoi.designsystem.Icon
import io.github.jeddchoi.thenewcafe.R

enum class TopLevelDestination(
    val selectedIcon: Icon,
    val unselectedIcon: Icon,
    val iconTextId: Int,
    val titleTextId: Int,
) {
    ACCOUNT(
        selectedIcon = Icon.ImageVectorIcon(CafeIcons.Account_Filled),
        unselectedIcon = Icon.ImageVectorIcon(CafeIcons.Account),
        iconTextId = R.string.account,
        titleTextId = R.string.account
    ),
    ORDER(
        selectedIcon = Icon.ImageVectorIcon(CafeIcons.Order_Filled),
        unselectedIcon = Icon.ImageVectorIcon(CafeIcons.Order),
        iconTextId = R.string.order,
        titleTextId = R.string.order
    ),
    MYPAGE(
        selectedIcon = Icon.ImageVectorIcon(CafeIcons.MyPage_Filled),
        unselectedIcon = Icon.ImageVectorIcon(CafeIcons.MyPage),
        iconTextId = R.string.mypage,
        titleTextId = R.string.mypage
    )
}