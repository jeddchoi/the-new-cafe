import {database} from "firebase-admin";
import RealtimeDatabaseUtil from "../util/RealtimeDatabaseUtil";
import {UserSession, UserStateChange} from "../model/UserSession";
import {UserStateType} from "../model/UserStateType";
import {UserStateChangeReason} from "../model/UserStateChangeReason";
import {SeatPosition} from "../model/UserState";
import {RequestType} from "../model/RequestType";
import {logger} from "firebase-functions/v2";

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
        logger.debug(`[UserSessionHandler] createSession(${timestamp}, ${seatPosition})`);
        return this.ref.set(<UserSession>{
            startTime: timestamp,
            seatPosition,
        });
    }

    addStateChange(requestType: RequestType, resultState: UserStateType | null, timestamp: number, reason: UserStateChangeReason, success: boolean) {
        logger.debug(`[UserSessionHandler] addStateChange(${requestType}, ${resultState}, ${timestamp}, ${reason}, ${success})`);
        return this.stateChangesRef.push(<UserStateChange>{
            resultState,
            timestamp,
            reason,
            requestType,
            success,
        });
    }

    cleanupSession(requestType: RequestType, timestamp: number) {
        logger.debug(`[UserSessionHandler] cleanupSession(${requestType}, ${timestamp})`);
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

