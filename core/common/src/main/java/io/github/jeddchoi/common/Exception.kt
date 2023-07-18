package io.github.jeddchoi.common

fun Throwable.toErrorMessage(vararg action: Action) = Message(
    title = UiText.StringResource(R.string.error),
    severity = Message.Severity.ERROR,
    content = UiText.DynamicString("[${currentTimeStr()}] ${message ?: stackTraceToString()}"),
    action = action.toList(),
)
