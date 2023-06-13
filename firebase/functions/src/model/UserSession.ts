import {SeatPosition} from "./SeatPosition";
import {UserStateChangeReason} from "./UserStateChangeReason";
import {UserStateType} from "./UserStateType";
import {RequestType} from "./RequestType";

interface UserStateChange {
    requestType: RequestType;
    resultState: UserStateType | undefined; // if state is changed successfully, resultState will not be undefined. If same, undefined
    timestamp: number;
    reason: UserStateChangeReason;
    success: boolean;
}


interface UserSession {
    startTime: number;
    endTime: number | null;
    seatPosition: SeatPosition | null;
    stateChanges: { [pushKey: string]: UserStateChange };
}

const START_TIME_PROPERTY_NAME = "startTime";

interface CompletedUserSession extends UserSession {
    endTime: number;
}

export {
    UserStateChange,
    UserSession,
    CompletedUserSession,
    START_TIME_PROPERTY_NAME,
};
