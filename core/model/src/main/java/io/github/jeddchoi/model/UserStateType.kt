package io.github.jeddchoi.model

enum class UserStateType {
    None,
    Reserved,
    Occupied,
    Away,
    OnBusiness;


    companion object {
        private val VALUES = values()
        fun getByValue(input: String?) =
            input?.let {
                val userStateStr = it.substringAfter("_")
                VALUES.firstOrNull { code -> code.name == userStateStr }
            } ?: None
    }
}