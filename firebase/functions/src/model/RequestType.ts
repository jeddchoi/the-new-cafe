import {UserStateType} from "./UserStateType";

export enum RequestType {
    ReserveSeat,
    OccupySeat,
    Quit,
    DoBusiness,
    ShiftToBusiness,
    LeaveAway,
    ResumeUsing,
    ChangeOverallTimeoutTime,
    ChangeTemporaryTimeoutTime,
}

export function resultStateByRequestType(requestType: RequestType, success: boolean) {
    if (!success) return null;
    switch (requestType) {
        case RequestType.ReserveSeat:
            return UserStateType.Reserved;
        case RequestType.Quit:
            return UserStateType.None;
        case RequestType.OccupySeat:
        case RequestType.ResumeUsing:
            return UserStateType.Occupied;
        case RequestType.DoBusiness:
        case RequestType.ShiftToBusiness:
            return UserStateType.OnBusiness;
        case RequestType.LeaveAway:
            return UserStateType.Away;
        case RequestType.ChangeOverallTimeoutTime:
        case RequestType.ChangeTemporaryTimeoutTime:
            return null;
    }
}
