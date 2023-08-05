package io.github.jeddchoi.designsystem

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import io.github.jeddchoi.common.Message

@Composable
fun Message.Severity.textColor() = when (this) {
    Message.Severity.INFO -> MaterialTheme.colorScheme.primary
    Message.Severity.WARNING -> MaterialTheme.colorScheme.warning
    Message.Severity.ERROR -> MaterialTheme.colorScheme.error
}