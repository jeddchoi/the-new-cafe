import {SeatStatusType} from "./Seat";
import {RequestType} from "./MyRequest";

enum UserStatusType {
    None,
    Reserved,
    Occupied,
    Away,
    OnBusiness,
    Blocked,
}

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
        defaultTimeoutAfterInSeconds: 100,
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
        defaultTimeoutAfterInSeconds: 100,
        seatStatus: SeatStatusType.None,
    },
};

enum UserStatusChangeReason {
    UserAction,
    Timeout,
    Admin,
}

interface ISeatPosition {
    storeId: string;
    sectionId: string;
    seatId: string;
}

interface ITimerTask {
    timerTaskName: string;
    startStatusAt: number;
    keepStatusUntil: number;
}

interface IUserStatus {
    uid: string;
    lastStatus: UserStatusType;
    status: UserStatusType;
    statusUpdatedAt: Date;
    statusUpdatedBy: UserStatusChangeReason;
    seatPosition: ISeatPosition | null;
    usageTimer: ITimerTask | null;
    currentTimer: ITimerTask | null;
}

interface IUserStatusExternal {
    lastStatus: number;
    status: number;
    statusUpdatedAt: number;
    statusUpdatedBy: number;
    seatPosition: ISeatPosition | null;
    usageTimer: ITimerTask | null;
    currentTimer: ITimerTask | null;
}

class UserStatus implements IUserStatus {
    constructor(
        readonly uid: string,
        readonly lastStatus: UserStatusType,
        readonly status: UserStatusType,
        readonly statusUpdatedAt: Date,
        readonly statusUpdatedBy: UserStatusChangeReason,
        readonly seatPosition: ISeatPosition | null,
        readonly usageTimer: ITimerTask | null,
        readonly currentTimer: ITimerTask | null,
    ) {
    }

    static fromExternal(uid: string, val: IUserStatusExternal): UserStatus {
        return new UserStatus(
            uid,
            val.lastStatus,
            val.status,
            new Date(val.statusUpdatedAt),
            val.statusUpdatedBy,
            val.seatPosition,
            val.usageTimer,
            val.currentTimer);
    }

    toString() {
        const properties = Object.entries(this)
            .map(([key, value]) => `${key}: ${JSON.stringify(value)}`)
            .join(", ");
        return `UserStatus { ${properties} }`;
    }
}

export {
    UserStatus,
    StatusInfo,
    UserStatusType,
    UserStatusChangeReason,
    ISeatPosition,
    ITimerTask,
    IUserStatus,
    IUserStatusExternal,
};
