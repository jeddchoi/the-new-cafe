import {SeatStateType} from "./Seat";
import {RequestType} from "./RequestType";
import {UserStateType} from "./UserStateType";

const StateInfo: {
    [key in UserStateType]: {
        requestTypeIfTimeout: RequestType,
        seatState: SeatStateType,
    }
} = {
    [UserStateType.None]: {
        requestTypeIfTimeout: RequestType.NoOp,
        seatState: SeatStateType.Empty,
    },
    [UserStateType.Reserved]: {
        requestTypeIfTimeout: RequestType.CancelReservation,
        seatState: SeatStateType.Reserved,
    },
    [UserStateType.Occupied]: {
        requestTypeIfTimeout: RequestType.StopUsingSeat,
        seatState: SeatStateType.Occupied,
    },
    [UserStateType.Away]: {
        requestTypeIfTimeout: RequestType.StopUsingSeat,
        seatState: SeatStateType.Away,
    },
    [UserStateType.OnBusiness]: {
        requestTypeIfTimeout: RequestType.FinishBusiness,
        seatState: SeatStateType.Away,
    },
    [UserStateType.Blocked]: {
        requestTypeIfTimeout: RequestType.Unblock,
        seatState: SeatStateType.Empty,
    },
};
export {StateInfo};
