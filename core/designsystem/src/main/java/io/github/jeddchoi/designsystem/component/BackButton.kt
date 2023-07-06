package io.github.jeddchoi.designsystem.component

import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import io.github.jeddchoi.common.CafeIcons

@Composable
fun BackButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.onBackground,
) {
    IconButton(onClick = {}) {
        Icon(CafeIcons.ArrowBack, "backIcon")
    }
}