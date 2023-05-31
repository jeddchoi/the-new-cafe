enum UserStatusType {
    None,
    Reserved,
    Occupied,
    Vacant,
    OnTask,
    Blocked,
}

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
    UserStatusType,
    UserStatusChangeReason,
    ISeatPosition,
    ITimerTask,
    IUserStatus,
    IUserStatusExternal,
};
