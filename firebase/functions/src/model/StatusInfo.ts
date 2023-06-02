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
        defaultTimeoutAfterInSeconds: 100,
        seatStatus: SeatStatusType.Reserved,
    },
    [UserStatusType.Occupied]: {
        requestTypeIfTimeout: RequestType.TimeoutOnUse,
        defaultTimeoutAfterInSeconds: 1000,
        seatStatus: SeatStatusType.Occupied,
    },
    [UserStatusType.Away]: {
        requestTypeIfTimeout: RequestType.TimeoutOnAway,
        defaultTimeoutAfterInSeconds: 100,
        seatStatus: SeatStatusType.Away,
    },
    [UserStatusType.OnBusiness]: {
        requestTypeIfTimeout: RequestType.TimeoutOnBusiness,
        defaultTimeoutAfterInSeconds: 100,
        seatStatus: SeatStatusType.Away,
    },
    [UserStatusType.Blocked]: {
        requestTypeIfTimeout: RequestType.Unblock,
        defaultTimeoutAfterInSeconds: 1000,
        seatStatus: SeatStatusType.None,
    },
};
export {StatusInfo};
