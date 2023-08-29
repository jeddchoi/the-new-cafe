package io.github.jeddchoi.firebase.functions

import io.github.jeddchoi.model.SeatFinderUserRequestType

enum class FirebaseSeatFinderRequestType {
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

fun FirebaseSeatFinderRequestType.toSeatFinderRequestType() =
    when (this) {
        FirebaseSeatFinderRequestType.ReserveSeat -> SeatFinderUserRequestType.Reserve
        FirebaseSeatFinderRequestType.OccupySeat -> SeatFinderUserRequestType.Occupy
        FirebaseSeatFinderRequestType.Quit -> SeatFinderUserRequestType.Quit
        FirebaseSeatFinderRequestType.DoBusiness -> SeatFinderUserRequestType.DoBusiness
        FirebaseSeatFinderRequestType.LeaveAway -> SeatFinderUserRequestType.LeaveAway
        FirebaseSeatFinderRequestType.ResumeUsing -> SeatFinderUserRequestType.ResumeUsing
        FirebaseSeatFinderRequestType.ChangeMainStateEndTime -> null
        FirebaseSeatFinderRequestType.ChangeSubStateEndTime -> null
    }