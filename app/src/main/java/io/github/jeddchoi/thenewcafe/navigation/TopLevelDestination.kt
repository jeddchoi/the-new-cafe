package io.github.jeddchoi.thenewcafe.navigation

import io.github.jeddchoi.account.AccountNavigation
import io.github.jeddchoi.designsystem.CafeIcons
import io.github.jeddchoi.designsystem.Icon
import io.github.jeddchoi.mypage.MyPageNavigation
import io.github.jeddchoi.store_list.StoreListNavigation
import io.github.jeddchoi.store.StoreNavigation
import io.github.jeddchoi.thenewcafe.R

/**
 * Top-level destination which is navigated to when pressed bottom navigation item.
 *
 * @property selectedIcon : Icon on selected
 * @property unselectedIcon : Icon on not selected
 * @property iconTextId : Label text in bottom navigation item
 * @property titleTextId : Title text in top app bar
 */
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

val appNavigations = listOf(AccountNavigation, StoreListNavigation, MyPageNavigation, StoreNavigation)