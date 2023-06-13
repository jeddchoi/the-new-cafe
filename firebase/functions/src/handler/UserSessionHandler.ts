import {database} from "firebase-admin";
import RealtimeDatabaseUtil from "../util/RealtimeDatabaseUtil";
import {UserSession, UserStateChange} from "../model/UserSession";
import {UserStateType} from "../model/UserStateType";
import {UserStateChangeReason} from "../model/UserStateChangeReason";
import {SeatPosition} from "../model/UserState";
import {RequestType} from "../model/RequestType";

export default class UserSessionHandler {
    private ref: database.Reference;
    private stateChangesRef: database.Reference;

    constructor(
        readonly userId: string
    ) {
        this.ref = RealtimeDatabaseUtil.getUserSessionRef(userId);
        this.stateChangesRef = this.ref.child("stateChanges");
    }

    createSession(timestamp: number, seatPosition: SeatPosition | null) {
        return this.ref.set(<UserSession>{
            startTime: timestamp,
            seatPosition,
        });
    }

    addStateChange(requestType: RequestType, resultState: UserStateType | null, timestamp: number, reason: UserStateChangeReason, success: boolean) {
        return this.stateChangesRef.push(<UserStateChange>{
            resultState,
            timestamp,
            reason,
            requestType,
            success,
        });
    }

    cleanupSession(requestType: RequestType, timestamp: number) {
        return this.ref.once("value")
            .then((snapshot) => {
                const userSession = snapshot.val() as UserSession;
                return RealtimeDatabaseUtil.getUserHistoryRef(this.userId).push(<UserSession>{
                    ...userSession,
                    endTime: timestamp,
                });
            }).then(() => {
                return this.ref.remove();
            });
    }
}

