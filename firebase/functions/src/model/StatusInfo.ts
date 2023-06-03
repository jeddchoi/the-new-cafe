import {SeatStatusType} from "./Seat";
import {RequestType} from "./RequestType";
import {UserStatusType} from "./UserStatusType";

const StatusInfo: {
    [key in UserStatusType]: {
        requestTypeIfTimeout: RequestType,
        seatStatus: SeatStatusType,
    }
} = {
    [UserStatusType.None]: {
        requestTypeIfTimeout: RequestType.NoOp,
        seatStatus: SeatStatusType.None,
    },
    [UserStatusType.Reserved]: {
        requestTypeIfTimeout: RequestType.CancelReservation,
        seatStatus: SeatStatusType.Reserved,
    },
    [UserStatusType.Occupied]: {
        requestTypeIfTimeout: RequestType.StopUsingSeat,
        seatStatus: SeatStatusType.Occupied,
    },
    [UserStatusType.Away]: {
        requestTypeIfTimeout: RequestType.StopUsingSeat,
        seatStatus: SeatStatusType.Away,
    },
    [UserStatusType.OnBusiness]: {
        requestTypeIfTimeout: RequestType.FinishBusiness,
        seatStatus: SeatStatusType.Away,
    },
    [UserStatusType.Blocked]: {
        requestTypeIfTimeout: RequestType.Unblock,
        seatStatus: SeatStatusType.None,
    },
};
export {StatusInfo};
