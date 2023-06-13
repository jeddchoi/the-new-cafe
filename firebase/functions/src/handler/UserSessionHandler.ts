import {database} from "firebase-admin";
import RealtimeDatabaseUtil from "../util/RealtimeDatabaseUtil";
import {UserSession, UserStateChange} from "../model/UserSession";
import {UserStateType} from "../model/UserStateType";
import {UserStateChangeReason} from "../model/UserStateChangeReason";
import {OverallState, State} from "../model/UserState";

export default class UserSessionHandler {
    private ref: database.Reference;
    private stateChangesRef: database.Reference;
    constructor(
        readonly userId: string
    ) {
        this.ref = RealtimeDatabaseUtil.getUserSessionRef(userId);
        this.stateChangesRef = this.ref.child("stateChanges");
    }

    createSession(overallState: OverallState) {
        return this.ref.set(<UserSession>{
            startTime: overallState.startTime,
            seatPosition: overallState.seatPosition,
        }).then(() => {
            return this.stateChangesRef.push(<UserStateChange>{
                state: overallState.state,
                timestamp: overallState.startTime,
                reason: overallState.reason,
            });
        });
    }
    addStateChange(state: State) {
        return this.stateChangesRef.push(<UserStateChange>{
            state: state.state,
            timestamp: state.startTime,
            reason: state.reason,
        });
    }

    addStateChangeOnDeletion(state: UserStateType, timestamp: number, reason: UserStateChangeReason) {
        return this.stateChangesRef.push(<UserStateChange>{
            state,
            timestamp,
            reason,
        });
    }

    cleanupSession(eventTimestamp: number) {
        return this.ref.once("value").then((snapshot) => {
            const userSession = snapshot.val() as UserSession;
            return RealtimeDatabaseUtil.getUserHistoryRef(this.userId).push(<UserSession>{
                ...userSession,
                endTime: eventTimestamp,
            });
        }).then(()=> {
            return this.ref.remove();
        });
    }
}

