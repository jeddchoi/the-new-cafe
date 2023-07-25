package io.github.jeddchoi.model


enum class SeatFinderUserRequestType(val availableState: List<UserStateType>, val resultState: UserStateType?) {
    Reserve(listOf(UserStateType.None), UserStateType.Reserved),
    Occupy(listOf(UserStateType.Reserved), UserStateType.Occupied),
    Quit(listOf(UserStateType.Reserved, UserStateType.Occupied, UserStateType.Away, UserStateType.OnBusiness), UserStateType.None),
    DoBusiness(listOf(UserStateType.Occupied, UserStateType.Away), UserStateType.OnBusiness),
    LeaveAway(listOf(UserStateType.Occupied), UserStateType.Away),
    ResumeUsing(listOf(UserStateType.Away, UserStateType.OnBusiness), UserStateType.Occupied),
    ChangeReservationEndTime(listOf(UserStateType.Reserved), null),
    ChangeOccupyEndTime(listOf(UserStateType.Occupied, UserStateType.Away, UserStateType.OnBusiness), null),
    ChangeBusinessEndTime(listOf(UserStateType.OnBusiness), null),
    ChangeAwayEndTime(listOf(UserStateType.Away), null),
    ;

    companion object {
        val RequestTypesInSession = values().toList().drop(1)
    }
}