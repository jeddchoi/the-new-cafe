import {UserStateType} from "./UserStateType";
import {UserStateChangeReason} from "./UserStateChangeReason";
import {SeatId} from "./SeatId";

interface TimerInfo {
    endTime: number;
    targetState: UserStateType;
    taskName: string;
}

interface State {
    reason: UserStateChangeReason;
    seatId: string | null;
    startTime: number;
    state: UserStateType;
    timer: TimerInfo | null;
}

interface OverallState extends State{
    sessionId: string | null;
}


interface IUserState {
    isOnline: boolean;
    name: string;
    status: {
        overall: OverallState,
        temporary: State
    };
    userId: string;
}

const CURRENT_TIMER_PROPERTY_NAME = "currentTimer";
const USAGE_TIMER_PROPERTY_NAME = "usageTimer";


interface IUserStateExternal {
    isOnline: boolean;
    name: string;
    status: {
        overall: OverallState,
        temporary: State
    };
}

class UserState implements IUserState {
    constructor(
        readonly isOnline: boolean,
        readonly name: string,
        readonly status: {
            overall: OverallState,
            temporary: State
        },
        readonly userId: string,
    ) {
    }

    static fromExternal(uid: string, val: IUserStateExternal): UserState {
        return new UserState(val.isOnline, val.name, val.status, uid);
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
    SeatId,
    TimerInfo,
    IUserState,
    IUserStateExternal,
    CURRENT_TIMER_PROPERTY_NAME,
    USAGE_TIMER_PROPERTY_NAME,
};
