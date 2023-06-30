package io.github.jeddchoi.ui.model

import io.github.jeddchoi.designsystem.Icon

interface NavScreen {
    val route: String
    val titleId: Int
    val selectedIcon: Icon
    val unselectedIcon: Icon
}