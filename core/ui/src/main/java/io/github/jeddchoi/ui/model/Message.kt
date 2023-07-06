package io.github.jeddchoi.ui.model

import io.github.jeddchoi.common.CafeIcons
import io.github.jeddchoi.common.UiIcon
import io.github.jeddchoi.common.UiText
import io.github.jeddchoi.ui.R

data class Message(
    val title: io.github.jeddchoi.common.UiText,
    val severity: MessageSeverity,
    val action: List<Action> = listOf(),
//    val contentId: Int? = null,
    val content: io.github.jeddchoi.common.UiText? = null,
)

enum class MessageSeverity(val icon: io.github.jeddchoi.common.UiIcon) {
    INFO(io.github.jeddchoi.common.UiIcon.ImageVectorIcon(io.github.jeddchoi.common.CafeIcons.Info, io.github.jeddchoi.common.UiText.StringResource(R.string.desc_info_message))),
    WARNING(io.github.jeddchoi.common.UiIcon.ImageVectorIcon(io.github.jeddchoi.common.CafeIcons.Warning, io.github.jeddchoi.common.UiText.StringResource(R.string.desc_warning_message))),
    ERROR(io.github.jeddchoi.common.UiIcon.ImageVectorIcon(io.github.jeddchoi.common.CafeIcons.Error, io.github.jeddchoi.common.UiText.StringResource(R.string.desc_error_message)))
}

data class Action(val title: io.github.jeddchoi.common.UiText, val action: () -> Unit)