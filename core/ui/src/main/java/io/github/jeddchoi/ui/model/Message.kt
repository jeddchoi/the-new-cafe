package io.github.jeddchoi.ui.model

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import io.github.jeddchoi.common.CafeIcons
import io.github.jeddchoi.common.UiIcon
import io.github.jeddchoi.common.UiText
import io.github.jeddchoi.ui.R

data class Message(
    val title: UiText,
    val severity: MessageSeverity,
    val action: List<Action> = listOf(),
//    val contentId: Int? = null,
    val content: UiText? = null,
)

enum class MessageSeverity(val icon: UiIcon) {
    INFO(UiIcon.ImageVectorIcon(CafeIcons.Info, UiText.StringResource(R.string.desc_info_message))),
    WARNING(UiIcon.ImageVectorIcon(CafeIcons.Warning, UiText.StringResource(R.string.desc_warning_message))),
    ERROR(UiIcon.ImageVectorIcon(CafeIcons.Error, UiText.StringResource(R.string.desc_error_message)))
    ;

    @Composable
    fun textColor() = when (this) {
        INFO -> MaterialTheme.colorScheme.primary
        WARNING -> MaterialTheme.colorScheme.errorContainer
        ERROR -> MaterialTheme.colorScheme.error
    }
}

data class Action(val title: UiText, val action: () -> Unit)