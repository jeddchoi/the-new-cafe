package io.github.jeddchoi.designsystem

import androidx.annotation.DrawableRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.DataUsage
import androidx.compose.material.icons.filled.ShoppingBasket
import androidx.compose.material.icons.filled.Store
import androidx.compose.material.icons.rounded.*
import androidx.compose.ui.graphics.vector.ImageVector

object CafeIcons {
    val Profile = Icons.Rounded.AccountCircle
    val Profile_Filled = Icons.Filled.AccountCircle
    val Order = Icons.Rounded.ShoppingBasket
    val Order_Filled = Icons.Filled.ShoppingBasket
    val MyPage = Icons.Rounded.DataUsage
    val MyPage_Filled = Icons.Filled.DataUsage
    val Store = Icons.Rounded.Store
    val Store_Filled = Icons.Filled.Store

    val Info = Icons.Rounded.Info
    val Warning = Icons.Rounded.Warning
    val Error = Icons.Rounded.Error

}

/**
 * A sealed class to make dealing with [ImageVector] and [DrawableRes] icons easier.
 */
sealed class Icon {
    data class ImageVectorIcon(val imageVector: ImageVector) : Icon()
    data class DrawableResourceIcon(@DrawableRes val id: Int) : Icon()
}
