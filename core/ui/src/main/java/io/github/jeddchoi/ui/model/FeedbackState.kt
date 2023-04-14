package io.github.jeddchoi.ui.model

interface FeedbackState {
    val isBusy: Boolean
    val canContinue: Boolean
    val messages: List<Message>

    fun copy(
        isBusy: Boolean = this.isBusy,
        canContinue: Boolean = this.canContinue,
        messages: List<Message> = this.messages
    ) : FeedbackState
}
