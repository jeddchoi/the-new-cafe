import {UserStateType} from "./UserStateType";
import {UserStateChangeReason} from "./UserStateChangeReason";

interface ISeatPosition {
    storeId: string;
    sectionId: string;
    seatId: string;
}

interface ITimerTask {
    timerTaskName: string;
    startStateAt: number;
    keepStateUntil: number;
}

interface IUserState {
    uid: string;
    lastState: UserStateType;
    state: UserStateType;
    stateUpdatedAt: Date;
    stateUpdatedBy: UserStateChangeReason;
    seatPosition: ISeatPosition | null;
    usageTimer: ITimerTask | null;
    currentTimer: ITimerTask | null;
}

const CURRENT_TIMER_PROPERTY_NAME = "currentTimer";
const USAGE_TIMER_PROPERTY_NAME = "usageTimer";


interface IUserStateExternal {
    lastState: number;
    state: number;
    stateUpdatedAt: number;
    stateUpdatedBy: number;
    seatPosition: ISeatPosition | null;
    usageTimer: ITimerTask | null;
    currentTimer: ITimerTask | null;
}

class UserState implements IUserState {
    constructor(
        readonly uid: string,
        readonly lastState: UserStateType,
        readonly state: UserStateType,
        readonly stateUpdatedAt: Date,
        readonly stateUpdatedBy: UserStateChangeReason,
        readonly seatPosition: ISeatPosition | null,
        readonly usageTimer: ITimerTask | null,
        readonly currentTimer: ITimerTask | null,
    ) {
    }

    static fromExternal(uid: string, val: IUserStateExternal): UserState {
        return new UserState(
            uid,
            val.lastState,
            val.state,
            new Date(val.stateUpdatedAt),
            val.stateUpdatedBy,
            val.seatPosition,
            val.usageTimer,
            val.currentTimer);
    }

    toString() {
        const properties = Object.entries(this)
            .map(([key, value]) => `${key}: ${JSON.stringify(value)}`)
            .join(", ");
        return `UserState { ${properties} }`;
    }
}

export {
    UserState,
    ISeatPosition,
    ITimerTask,
    IUserState,
    IUserStateExternal,
    CURRENT_TIMER_PROPERTY_NAME,
    USAGE_TIMER_PROPERTY_NAME,
};
