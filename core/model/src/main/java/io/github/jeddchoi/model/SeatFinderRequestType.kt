package io.github.jeddchoi.model

enum class SeatFinderRequestType(val resultState: UserStateType?) {
    ReserveSeat(UserStateType.Reserved),
    OccupySeat(UserStateType.Occupied),
    Quit(UserStateType.None),
    DoBusiness(UserStateType.OnBusiness),
    LeaveAway(UserStateType.Away),
    ResumeUsing(UserStateType.Occupied),
    ChangeMainStateEndTime(null),
    ChangeSubStateEndTime(null),
    ;

    companion object {
        private val VALUES = values()
        fun getByValue(input: String?) =
            input?.let { VALUES.firstOrNull { code -> code.name == it } }
    }
}