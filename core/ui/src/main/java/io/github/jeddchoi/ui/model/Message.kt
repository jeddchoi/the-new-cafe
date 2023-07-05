package io.github.jeddchoi.ui.model

import io.github.jeddchoi.designsystem.CafeIcons
import io.github.jeddchoi.designsystem.UiIcon
import io.github.jeddchoi.designsystem.UiText
import io.github.jeddchoi.ui.R

data class Message(
    val title: Int,
    val severity: Severity,
    val action: List<Action> = listOf(),
    val contentId: Int? = null,
    val content: String? = null,
)

enum class Severity(val icon: UiIcon) {
    INFO(UiIcon.ImageVectorIcon(CafeIcons.Info, UiText.StringResource(R.string.desc_info_message))),
    WARNING(UiIcon.ImageVectorIcon(CafeIcons.Warning, UiText.StringResource(R.string.desc_warning_message))),
    ERROR(UiIcon.ImageVectorIcon(CafeIcons.Error, UiText.StringResource(R.string.desc_error_message)))
}

data class Action(val titleId: Int, val action: () -> Unit)