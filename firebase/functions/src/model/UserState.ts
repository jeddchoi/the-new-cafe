import {UserStateType} from "./UserStateType";
import {UserStateChangeReason} from "./UserStateChangeReason";
import {SeatId} from "./SeatId";

interface TimerInfo {
    endTime: number;
    taskName: string;
}

interface TemporaryTimerInfo extends TimerInfo{
    isReset: boolean
}

interface State {
    state: UserStateType;
    reason: UserStateChangeReason;
    startTime: number;
    timer: TimerInfo | null;
}

interface OverallState extends State{
    seatId: string | null;
}

interface TemporaryState extends State {
    timer: TemporaryTimerInfo | null;
}


interface IUserState {
    isOnline: boolean;
    name: string;
    status: {
        overall: OverallState,
        temporary: TemporaryState
    } | null;
    userId: string;
}

const OVERALL_STATE_PROPERTY_NAME = "overall";
const TEMPORARY_STATE_PROPERTY_NAME = "temporary";


interface IUserStateExternal {
    isOnline: boolean;
    name: string;
    status: {
        overall: OverallState,
        temporary: TemporaryState
    } | null;
}

class UserState implements IUserState {
    constructor(
        readonly isOnline: boolean,
        readonly name: string,
        readonly status: {
            overall: OverallState,
            temporary: TemporaryState
        } | null,
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
    OverallState,
    TemporaryState,
    SeatId,
    TimerInfo,
    TemporaryTimerInfo,
    IUserState,
    IUserStateExternal,
    OVERALL_STATE_PROPERTY_NAME,
    TEMPORARY_STATE_PROPERTY_NAME,
};
