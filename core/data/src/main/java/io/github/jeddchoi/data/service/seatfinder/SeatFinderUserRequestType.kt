package io.github.jeddchoi.data.service.seatfinder

import androidx.annotation.StringRes
import io.github.jeddchoi.common.UiText
import io.github.jeddchoi.data.R
import io.github.jeddchoi.model.UserStateType

enum class SeatFinderUserRequestType(@StringRes val title: Int, val availableState: List<UserStateType>) {
    Reserve(R.string.reserve, listOf(UserStateType.None)),
    Occupy(R.string.occupy, listOf(UserStateType.Reserved)),
    Quit(R.string.quit, listOf(UserStateType.Reserved, UserStateType.Occupied, UserStateType.Away, UserStateType.OnBusiness)),
    DoBusiness(R.string.do_business, listOf(UserStateType.Occupied, UserStateType.Away)),
    LeaveAway(R.string.leave_away, listOf(UserStateType.Occupied)),
    ResumeUsing(R.string.resume_using, listOf(UserStateType.Away, UserStateType.OnBusiness)),
    ChangeReservationEndTime(R.string.change_reservation_end_time, listOf(UserStateType.Reserved)),
    ChangeOccupyEndTime(R.string.change_occupy_end_time, listOf(UserStateType.Occupied, UserStateType.Away, UserStateType.OnBusiness)),
    ChangeBusinessEndTime(R.string.change_business_end_time, listOf(UserStateType.OnBusiness)),
    ChangeAwayEndTime(R.string.change_away_end_time, listOf(UserStateType.Away)),
    ;

    fun toUiText() = UiText.StringResource(title)
    companion object {
        val RequestTypesInSession = values().toList().drop(1)
    }
}