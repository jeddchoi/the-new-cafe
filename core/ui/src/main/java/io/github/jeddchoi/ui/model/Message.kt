package io.github.jeddchoi.ui.model

import io.github.jeddchoi.designsystem.CafeIcons
import io.github.jeddchoi.designsystem.Icon

data class Message(
    val titleId: Int,
    val severity: Severity,
    val action: List<Action> = listOf(),
    val contentId: Int? = null,
    val content: String? = null,
)

enum class Severity(val icon: Icon) {
    INFO(Icon.ImageVectorIcon(CafeIcons.Info)),
    WARNING(Icon.ImageVectorIcon(CafeIcons.Warning)),
    ERROR(Icon.ImageVectorIcon(CafeIcons.Error))
}

data class Action(val titleId: Int, val action: () -> Unit)