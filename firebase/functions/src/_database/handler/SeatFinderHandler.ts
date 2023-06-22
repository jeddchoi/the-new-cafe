import {databaseUtil, DatabaseUtil} from "../DatabaseUtil";
import {database} from "firebase-admin";
import {https, logger} from "firebase-functions/v2";
import {TransactionResult} from "../../helper/TransactionResult";
import {UserStateChange} from "../model/UserStateChange";
import {CurrentSession} from "../model/CurrentSession";
import {Seat} from "../../_firestore/model/Seat";
import {SeatFinderRequestType} from "../../seat-finder/_enum/SeatFinderRequestType";
import SessionHandler from "./SessionHandler";
import SeatHandler from "../../_firestore/handler/SeatHandler";
import {SeatFinderRequest} from "../../seat-finder/_model/SeatFinderRequest";
import {ResultCode} from "../../seat-finder/_enum/ResultCode";
import {UserMainStateType} from "../../seat-finder/_enum/UserMainStateType";

const REFERENCE_STATE_CHANGES_NAME = "stateChanges";
const REFERENCE_HISTORY_NAME = "history";
export default class SeatFinderHandler {
    private readonly sessionHandler: SessionHandler;
    private readonly seatHandler: SeatHandler;
    private readonly databaseUtil: DatabaseUtil;
    private stateChangesRef: database.Reference;
    private userHistoryRef: database.Reference;

    constructor(
        readonly userId: string,
    ) {
        this.sessionHandler = new SessionHandler(this.userId);
        this.seatHandler = new SeatHandler(this.userId);

        this.databaseUtil = databaseUtil ?? new DatabaseUtil();
        const seatFinderRef = this.databaseUtil.seatFinderRef();
        this.stateChangesRef = seatFinderRef.child(REFERENCE_STATE_CHANGES_NAME);
        this.userHistoryRef = seatFinderRef.child(REFERENCE_HISTORY_NAME).child(userId);
    }

    handleSeatFinderRequest(request: SeatFinderRequest) {
        const current = Date.now();
        const endTime = request.getEndTime(current);
        switch (request.requestType) {
            case SeatFinderRequestType.ReserveSeat: {
                if (!request.seatPosition) {
                    throw new https.HttpsError("invalid-argument", "Seat position is required");
                }
                const seatPosition = request.seatPosition;
                return this.createSession(
                    () => this.seatHandler.reserveSeat(seatPosition, endTime),
                    (sessionId: string) => this.sessionHandler.reserveSeat(sessionId, seatPosition, current, endTime),
                );
            }
            case SeatFinderRequestType.OccupySeat: {
                return this.transaction(
                    () => this.sessionHandler.occupySeat(current, endTime),
                    (currentSession: CurrentSession) => this.seatHandler.occupySeat(currentSession.seatPosition, endTime),
                );
            }
            case SeatFinderRequestType.LeaveAway: {
                return this.transaction(
                    () => this.sessionHandler.leaveAway(current, endTime),
                    (currentSession: CurrentSession) => this.seatHandler.away(currentSession.seatPosition),
                );
            }
            case SeatFinderRequestType.DoBusiness: {
                return this.transaction(
                    () => this.sessionHandler.doBusiness(current, endTime),
                    (currentSession: CurrentSession) => this.seatHandler.away(currentSession.seatPosition),
                );
            }
            case SeatFinderRequestType.ResumeUsing: {
                return this.transaction(
                    () => this.sessionHandler.resumeUsing(),
                    (currentSession: CurrentSession) => this.seatHandler.resumeUsing(currentSession.seatPosition),
                );
            }
            case SeatFinderRequestType.ChangeMainStateEndTime: {
                return this.transaction(
                    () => this.sessionHandler.changeMainStateEndTime(current, endTime),
                    (currentSession: CurrentSession) => {
                        switch (currentSession.mainState.state) {
                            case UserMainStateType.Reserved:
                                return this.seatHandler.changeReserveEndTime(currentSession.seatPosition, endTime);
                            case UserMainStateType.Occupied:
                                return this.seatHandler.changeOccupyEndTime(currentSession.seatPosition, endTime);
                        }
                    },
                );
            }
            case SeatFinderRequestType.ChangeSubStateEndTime: {
                // TODO: handle it
                return Promise.resolve();
            }
            case SeatFinderRequestType.Quit: {
                // TODO: handle it
                return Promise.resolve();
            }
            default: {
                logger.error("Unknown request type", {request});
                throw new https.HttpsError("unimplemented", `Unknown request type : ${JSON.stringify(request)}`);
            }
        }
    }

    // TODO: addStateChange
    async createSession(
        handleSeat: () => Promise<TransactionResult<Seat>>,
        handleSession: (sessionId: string) => Promise<TransactionResult<CurrentSession>>,
    ) {
        logger.debug("[StateChangeHandler] createSession");
        //
        // try {
        //     const sessionId = this.newSessionKey();
        //     const seatResult = await handleSeat().catch((seatError) => {
        //         logger.error("[StateChangeHandler] handleSeat error", {seatError});
        //         throw seatError;
        //     });
        //     logger.debug("[StateChangeHandler] handleSeat success", {seatResult});
        //
        //     const sessionResult = await handleSession(sessionId).catch(async (sessionError) => {
        //         await seatResult.rollback();
        //         throw sessionError;
        //     });
        //     logger.debug("[StateChangeHandler] handleSession success", {sessionResult});
        //
        //     if (!sessionResult.after) {
        //         throw new https.HttpsError("invalid-argument", "After session is deleted", {sessionResult});
        //     }
        //     await this.addStateChange(sessionId, <UserStateChange>{
        //         requestType: SeatFinderRequestType.ReserveSeat,
        //         timestamp: sessionResult.after.startSessionTime,
        //         resultState: sessionResult.after.mainState.state,
        //         reason: UserStateChangeReason.UserAction,
        //         success: true
        //     });
        // } catch (error) {
        //     logger.error("[StateChangeHandler] createSession error", {error});
        //     // TODO: add state change false and cleanup session
        //     await this.addStateChange(sessionId, stateChange);
        //     throw error;
        // }
    }

    transaction(
        handleSession: () => Promise<TransactionResult<CurrentSession>>,
        handleSeat: (currentSession: CurrentSession) => Promise<TransactionResult<Seat>>,
    ) {
        logger.debug("[StateChangeHandler] transaction");
        return handleSession().then((sessionResult) => {
            logger.debug("[StateChangeHandler] handleSession success", {sessionResult});
            if (!sessionResult.after?.seatPosition) {
                throw ResultCode.INVALID_SESSION_STATE;
            }
            return handleSeat(sessionResult.after).catch((err) => {
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

    private newSessionKey() {
        const sessionId = this.stateChangesRef.push().key;
        if (!sessionId) throw new https.HttpsError("unknown", "You are pushing on root");
        logger.debug("[StateChangeHandler] Creating new session", {sessionId});
        return sessionId;
    }

    private addStateChange(sessionId: string, stateChange: UserStateChange) {
        logger.debug("[StateChangeHandler] Add state change", {sessionId, stateChange});
        return this.stateChangesRef.child(sessionId).push(stateChange);
    }

    private cleanupSession(session: CurrentSession) {
        logger.debug("[StateChangeHandler] cleanupSession");
        // return this.userHistoryRef.child(session.sessionId).set();
    }
}
