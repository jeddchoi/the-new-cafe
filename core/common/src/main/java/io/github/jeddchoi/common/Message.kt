package io.github.jeddchoi.common

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable

data class Message(
    val title: UiText,
    val severity: MessageSeverity,
    val action: List<Action> = listOf(),
    val content: UiText? = null,
)

enum class MessageSeverity(val icon: UiIcon) {
    INFO(UiIcon.ImageVectorIcon(CafeIcons.Info, UiText.StringResource(R.string.desc_info_message))),
    WARNING(
        UiIcon.ImageVectorIcon(
            CafeIcons.Warning,
            UiText.StringResource(R.string.desc_warning_message)
        )
    ),
    ERROR(
        UiIcon.ImageVectorIcon(
            CafeIcons.Error,
            UiText.StringResource(R.string.desc_error_message)
        )
    )
    ;

    @Composable
    fun textColor() = when (this) {
        INFO -> MaterialTheme.colorScheme.primary
        WARNING -> MaterialTheme.colorScheme.errorContainer
        ERROR -> MaterialTheme.colorScheme.error
    }
}


open class Action(val title: UiText, val action: () -> Unit = {}) {
    class Retry(action: () -> Unit) :
        Action(title = UiText.StringResource(R.string.retry), action = action)

    class Dismiss(action: () -> Unit) :
        Action(title = UiText.StringResource(R.string.dimiss), action = action)
}
