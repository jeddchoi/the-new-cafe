package io.github.jeddchoi.data.service.seatfinder

enum class SeatFinderRequestType {
    ReserveSeat,
    OccupySeat,
    Quit,
    DoBusiness,
    ShiftToBusiness,
    LeaveAway,
    ResumeUsing,
    ChangeMainStateEndTime,
    ChangeSubStateEndTime,
    ;

    companion object {
        private val VALUES = values()
        fun getByValue(input: String?) =
            input?.let { VALUES.firstOrNull { code -> code.name == it } }
    }
}