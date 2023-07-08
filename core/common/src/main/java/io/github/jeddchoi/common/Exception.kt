package io.github.jeddchoi.common

fun Throwable.toErrorMessage(vararg action: Action) = Message(
    title = UiText.StringResource(R.string.error),
    severity = MessageSeverity.ERROR,
    content = UiText.DynamicString("[${getCurrentTime()}] ${message ?: stackTraceToString()}"),
    action = action.toList(),
)
