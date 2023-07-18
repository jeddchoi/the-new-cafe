package io.github.jeddchoi.model

enum class Sex {
    Male,
    Female,
    ;


    companion object {
        private val VALUES = Sex.values()
        fun getByValue(input: Int?) =
            input?.let {
                Sex.VALUES.firstOrNull { code -> code.ordinal == input }
            }
    }
}