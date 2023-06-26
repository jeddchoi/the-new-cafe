package io.github.jeddchoi.data.service

enum class SeatFinderRequestType {
    ReserveSeat,
    OccupySeat,
    Quit,
    DoBusiness,
    ShiftToBusiness,
    LeaveAway,
    ResumeUsing,
    ChangeOverallTimeoutTime,
    ChangeTemporaryTimeoutTime,
    ;

    companion object {
        private val VALUES = values()
        fun getByValue(input: String?) =
            input?.let { VALUES.firstOrNull { code -> code.name == it } }
    }
}