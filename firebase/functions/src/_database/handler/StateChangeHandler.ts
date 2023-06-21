import {databaseUtil, DatabaseUtil} from "../DatabaseUtil";
import {database} from "firebase-admin";
import {https, logger} from "firebase-functions/v2";
import {TransactionResult} from "../../helper/TransactionResult";
import {UserStateChange} from "../model/UserStateChange";
import {CurrentSession} from "../model/CurrentSession";
import {Seat} from "../../_firestore/model/Seat";
import {SeatPosition} from "../../_firestore/model/SeatPosition";

const REFERENCE_STATE_CHANGES_NAME = "stateChanges";
export default class StateChangeHandler {
    private readonly databaseUtil: DatabaseUtil;
    private stateChangesRef: database.Reference;

    constructor(
        readonly userId: string
    ) {
        this.databaseUtil = databaseUtil ?? new DatabaseUtil();
        const seatFinderRef = this.databaseUtil.seatFinderRef();
        this.stateChangesRef = seatFinderRef.child(REFERENCE_STATE_CHANGES_NAME);
    }

    newSessionKey() {
        const sessionId = this.stateChangesRef.push().key;
        if (!sessionId) throw new https.HttpsError("unknown", "You are pushing on root");
        logger.debug("[StateChangeHandler] Creating new session", {sessionId});
        return sessionId;
    }

    addStateChange(sessionId: string, stateChange: UserStateChange) {
        return this.stateChangesRef.child(sessionId).push(stateChange);
    }

    // TODO: addStateChange
    createSession(
        handleSeat: () => Promise<TransactionResult<Seat>>,
        handleSession: (sessionId: string) => Promise<TransactionResult<CurrentSession>>
    ) {
        logger.debug("[StateChangeHandler] createSession");
        const sessionId = this.newSessionKey();
        return handleSeat().then((seatResult) => {
            logger.debug("[StateChangeHandler] handleSeat success", {seatResult});
            return handleSession(sessionId).catch((err) => {
                logger.error("[StateChangeHandler] handleSession error", {err});
                return seatResult.rollback().then(() => {
                    logger.warn("[StateChangeHandler] rollback success", {err});
                    throw err;
                });
            }).then((sessionResult) => {
                logger.debug("[StateChangeHandler] handleSession success", {sessionResult});
            });
        });
    }

    transaction(
        handleSession: () => Promise<TransactionResult<CurrentSession>>,
        handleSeat: (seatPosition: SeatPosition) => Promise<TransactionResult<Seat>>,
    ) {
        logger.debug("[StateChangeHandler] transaction");
        return handleSession().then((sessionResult) => {
            logger.debug("[StateChangeHandler] handleSession success", {sessionResult});
            if (!sessionResult.after?.seatPosition) {
                throw new https.HttpsError("invalid-argument", "After session is deleted", {sessionResult});
            }
            return handleSeat(sessionResult.after.seatPosition).catch((err) => {
                logger.error("[StateChangeHandler] handleSeat error", {err});
                return sessionResult.rollback().then(() => {
                    logger.warn("[StateChangeHandler] rollback success", {err});
                    throw err;
                });
            }).then((seatResult) => {
                logger.debug("[StateChangeHandler] handleSeat success", {seatResult});
            });
        });
    }


    cleanupSession(
        handleSession: () => Promise<TransactionResult<CurrentSession>>,
        handleSeat: (seatPosition: SeatPosition) => Promise<TransactionResult<Seat>>,
    ) {
        logger.debug("[StateChangeHandler] cleanupSession");
        return this.transaction(handleSession, handleSeat);
    }
}
