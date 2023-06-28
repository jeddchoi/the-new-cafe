package io.github.jeddchoi.model

enum class SeatFinderRequestType() {
    ReserveSeat,
    OccupySeat,
    Quit,
    DoBusiness,
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