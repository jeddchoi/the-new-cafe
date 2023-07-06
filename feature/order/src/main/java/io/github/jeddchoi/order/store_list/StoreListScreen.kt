package io.github.jeddchoi.order.store_list

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import io.github.jeddchoi.common.UiText
import io.github.jeddchoi.order.R
import io.github.jeddchoi.ui.feature.PlaceholderScreen

@Composable
internal fun StoreListScreen(
    modifier: Modifier = Modifier
) {
    PlaceholderScreen(
        title = UiText.StringResource(R.string.store_list),
        modifier = modifier,
    )
}