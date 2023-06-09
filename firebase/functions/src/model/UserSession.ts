import {SeatId} from "./SeatId";
import {UserStateChangeReason} from "./UserStateChangeReason";

interface UserStateChange {
    state: number;
    timestamp: number;
    reason: UserStateChangeReason;
}


interface UserSession {
    startTime: number;
    endTime: number | null;
    seatId: SeatId | null;
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
