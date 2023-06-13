import {UserStateType} from "./UserStateType";

export enum RequestType {
    ReserveSeat,

    CancelReservation,
    OccupySeat,
    StopUsingSeat,
    DoBusiness,
    ShiftToBusiness,
    LeaveAway,
    ResumeUsing,
    ChangeTemporaryTimeoutTime,
    ChangeOverallTimeoutTime,
}

export function resultStateByRequestType(requestType: RequestType, success: boolean) {
    if (!success) return undefined;
    switch (requestType) {
        case RequestType.ReserveSeat:
            return UserStateType.Reserved;
        case RequestType.CancelReservation:
        case RequestType.StopUsingSeat:
            return UserStateType.None;
        case RequestType.OccupySeat:
        case RequestType.ResumeUsing:
            return UserStateType.Occupied;
        case RequestType.DoBusiness:
        case RequestType.ShiftToBusiness:
            return UserStateType.OnBusiness;
        case RequestType.LeaveAway:
            return UserStateType.Away;
        case RequestType.ChangeTemporaryTimeoutTime:
        case RequestType.ChangeOverallTimeoutTime:
            return undefined;
    }
}
