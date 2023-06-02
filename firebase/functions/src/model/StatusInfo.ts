import {SeatStatusType} from "./Seat";
import {RequestType} from "./RequestType";
import {UserStatusType} from "./UserStatusType";

const StatusInfo: {
    [key in UserStatusType]: {
        requestTypeIfTimeout: RequestType,
        defaultTimeoutAfterInSeconds: number | undefined,
        seatStatus: SeatStatusType,
    }
} = {
    [UserStatusType.None]: {
        requestTypeIfTimeout: RequestType.NoOp,
        defaultTimeoutAfterInSeconds: undefined,
        seatStatus: SeatStatusType.None,
    },
    [UserStatusType.Reserved]: {
        requestTypeIfTimeout: RequestType.TimeoutOnReserve,
        defaultTimeoutAfterInSeconds: undefined,
        seatStatus: SeatStatusType.Reserved,
    },
    [UserStatusType.Occupied]: {
        requestTypeIfTimeout: RequestType.TimeoutOnUse,
        defaultTimeoutAfterInSeconds: undefined,
        seatStatus: SeatStatusType.Occupied,
    },
    [UserStatusType.Away]: {
        requestTypeIfTimeout: RequestType.TimeoutOnAway,
        defaultTimeoutAfterInSeconds: 100,
        seatStatus: SeatStatusType.Away,
    },
    [UserStatusType.OnBusiness]: {
        requestTypeIfTimeout: RequestType.TimeoutOnBusiness,
        defaultTimeoutAfterInSeconds: undefined,
        seatStatus: SeatStatusType.Away,
    },
    [UserStatusType.Blocked]: {
        requestTypeIfTimeout: RequestType.Unblock,
        defaultTimeoutAfterInSeconds: undefined,
        seatStatus: SeatStatusType.None,
    },
};
export {StatusInfo};
