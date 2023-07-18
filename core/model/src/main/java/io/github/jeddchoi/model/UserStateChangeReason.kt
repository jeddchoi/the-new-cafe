package io.github.jeddchoi.model

enum class UserStateChangeReason {
    UserAction,
    Timeout,
    Admin,
    ;

    companion object {
        private val VALUES = UserStateChangeReason.values()
        fun getByValue(input: String?) =
            input?.let {
                VALUES.firstOrNull { code -> code.name == it }
            } ?: UserStateChangeReason.UserAction
    }
}