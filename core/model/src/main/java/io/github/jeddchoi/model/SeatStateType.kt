package io.github.jeddchoi.model

enum class SeatStateType {
    Empty,
    Reserved,
    Occupied,
    Away,
    Restricted,
    ;

    companion object {
        private val VALUES = SeatStateType.values()
        fun getByValue(input: String?) =
            input?.let {
                val seatStateStr = it.substringAfter("_")
                SeatStateType.VALUES.firstOrNull { code -> code.name == seatStateStr }
            } ?: Empty
    }
}