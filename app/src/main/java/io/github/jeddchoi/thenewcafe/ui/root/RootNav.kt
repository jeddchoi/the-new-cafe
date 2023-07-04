package io.github.jeddchoi.thenewcafe.ui.root

import androidx.annotation.StringRes
import io.github.jeddchoi.thenewcafe.R

enum class RootNav(val route: String, @StringRes val titleId: Int,) {
    Auth("auth", R.string.auth),
    Main("main", R.string.main),
}