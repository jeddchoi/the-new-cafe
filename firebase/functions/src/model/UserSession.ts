import {SeatId} from "./SeatId";
import {UserStateChangeReason} from "./UserStateChangeReason";

interface UserStateChange {
    state: number;
    timestamp: number;
    reason: UserStateChangeReason;
}


interface UserSession {
    sessionId: string;
    startTime: number;
    seatId: SeatId | null;
    stateChanges:UserStateChange[];
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
