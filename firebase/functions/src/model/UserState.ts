import {UserStateType} from "./UserStateType";
import {UserStateChangeReason} from "./UserStateChangeReason";
import {SeatPosition} from "./SeatPosition";
import {RequestType} from "./RequestType";

interface TimerInfo {
    endTime: number;
    taskName: string;
    willRequestType: RequestType,
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
    seatPosition: string | null;
}

interface TemporaryState extends State {
    timer: TemporaryTimerInfo | null;
}


interface IUserState {
    isOnline: boolean;
    name: string;
    status: {
        overall: OverallState,
        temporary: TemporaryState | null,
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
        temporary: TemporaryState | null,
    } | null;
}

class UserState implements IUserState {
    constructor(
        readonly isOnline: boolean,
        readonly name: string,
        readonly status: {
            overall: OverallState,
            temporary: TemporaryState | null,
        } | null,
        readonly userId: string,
    ) {
    }

    static fromExternal(uid: string, val: IUserStateExternal) {
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
    State,
    OverallState,
    TemporaryState,
    SeatPosition,
    TimerInfo,
    TemporaryTimerInfo,
    IUserState,
    IUserStateExternal,
    OVERALL_STATE_PROPERTY_NAME,
    TEMPORARY_STATE_PROPERTY_NAME,
};
