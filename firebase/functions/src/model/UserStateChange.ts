import {UserStateType} from "./UserStateType";
import {UserStateChangeReason} from "./UserStateChangeReason";
import {SeatId} from "./SeatId";

interface IUserStateChange {
    userId: string;
    prevState: UserStateType;
    nextState: UserStateType;
    stateUpdatedAt: Date;
    stateUpdatedBy: UserStateChangeReason;
    seatPosition: SeatId | null;
}

interface IUserStateChangeExternal {
    prevState: number;
    nextState: number;
    stateUpdatedBy: number;
    seatPosition: SeatId | null;
}

class UserStateChange implements IUserStateChange {
    constructor(
        readonly userId: string,
        readonly prevState: UserStateType,
        readonly nextState: UserStateType,
        readonly stateUpdatedAt: Date,
        readonly stateUpdatedBy: UserStateChangeReason,
        readonly seatPosition: SeatId | null,
    ) {
    }

    static fromExternal(userId: string, stateUpdatedAt: number, val: IUserStateChangeExternal): UserStateChange {
        return new UserStateChange(
            userId,
            val.prevState,
            val.nextState,
            new Date(stateUpdatedAt),
            val.stateUpdatedBy,
            val.seatPosition);
    }
}

export {IUserStateChange, IUserStateChangeExternal, UserStateChange};

