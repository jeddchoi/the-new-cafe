package io.github.jeddchoi.designsystem

import androidx.annotation.DrawableRes
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource

/**
 * A sealed class to make dealing with [ImageVector] and [DrawableRes] icons easier.
 */
sealed class UiIcon {
    abstract val contentDescription: UiText?

    data class ImageVectorIcon(
        val imageVector: ImageVector,
        override val contentDescription: UiText? = null
    ) : UiIcon()

    data class DrawableResourceIcon(
        @DrawableRes val id: Int,
        override val contentDescription: UiText? = null
    ) : UiIcon()

    @Composable
    fun IconComposable() = when (this) {
        is ImageVectorIcon ->
            Icon(
                imageVector = imageVector,
                contentDescription = contentDescription?.asString(),
            )

        is DrawableResourceIcon -> Icon(
            painter = painterResource(id = id),
            contentDescription = contentDescription?.asString(),
        )
    }

}