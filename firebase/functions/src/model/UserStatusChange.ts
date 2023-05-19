import {ISeatPosition, UserStatusChangeReason, UserStatusType} from "./UserStatus";

interface IUserStatusChange {
    userId: string;
    prevStatus: UserStatusType;
    nextStatus: UserStatusType;
    statusUpdatedAt: Date;
    statusUpdatedBy: UserStatusChangeReason;
    seatPosition: ISeatPosition | null;
}

interface IUserStatusChangeExternal {
    prevStatus: number;
    nextStatus: number;
    statusUpdatedBy: number;
    seatPosition: ISeatPosition | null;
}

class UserStatusChange implements IUserStatusChange {
    constructor(
        readonly userId: string,
        readonly prevStatus: UserStatusType,
        readonly nextStatus: UserStatusType,
        readonly statusUpdatedAt: Date,
        readonly statusUpdatedBy: UserStatusChangeReason,
        readonly seatPosition: ISeatPosition | null,
    ) {
    }

    static fromExternal(userId: string, statusUpdatedAt: number, val: IUserStatusChangeExternal): UserStatusChange {
        return new UserStatusChange(
            userId,
            val.prevStatus,
            val.nextStatus,
            new Date(statusUpdatedAt),
            val.statusUpdatedBy,
            val.seatPosition);
    }
}

export {IUserStatusChange, IUserStatusChangeExternal, UserStatusChange};

