package io.github.jeddchoi.data.service.seatfinder

import io.github.jeddchoi.model.UserStateType

enum class SeatFinderUserRequestType(val availableState: List<UserStateType>) {
    Reserve(listOf(UserStateType.None)),
    Occupy(listOf(UserStateType.Reserved)),
    Quit(listOf(UserStateType.Reserved, UserStateType.Occupied, UserStateType.Away, UserStateType.OnBusiness)),
    DoBusiness(listOf(UserStateType.Occupied, UserStateType.Away)),
    LeaveAway(listOf(UserStateType.Occupied)),
    ResumeUsing(listOf(UserStateType.Away, UserStateType.OnBusiness)),
    ChangeReservationEndTime(listOf(UserStateType.Reserved)),
    ChangeOccupyEndTime(listOf(UserStateType.Occupied, UserStateType.Away, UserStateType.OnBusiness)),
    ChangeBusinessEndTime(listOf(UserStateType.OnBusiness)),
    ChangeAwayEndTime(listOf(UserStateType.Away)),
}