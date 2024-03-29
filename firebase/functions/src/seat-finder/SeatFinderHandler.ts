import {DatabaseUtil} from "../_database/DatabaseUtil";
import {database} from "firebase-admin";
import {https, logger} from "firebase-functions/v2";
import {TransactionResult} from "../helper/TransactionResult";
import {SeatFinderEvent} from "../_database/model/SeatFinderEvent";
import {CurrentSession} from "../_database/model/CurrentSession";
import {Seat} from "../_firestore/model/Seat";
import {SeatFinderRequestType} from "./_enum/SeatFinderRequestType";
import SessionHandler from "../_database/SessionHandler";
import SeatHandler from "../_firestore/SeatHandler";
import {ResultCode} from "./_enum/ResultCode";
import {UserMainStateType} from "./_enum/UserMainStateType";
import {SeatPosition} from "../_firestore/model/SeatPosition";
import {SeatFinderEventBy} from "./_enum/SeatFinderEventBy";
import {PreviousSession} from "../_database/model/PreviousSession";
import {SeatFinderResult} from "./_model/SeatFinderResult";
import {REFERENCE_HISTORY_NAME, REFERENCE_STATE_CHANGES_NAME} from "../_database/NameConstant";
import {getEndTime} from "./_model/SeatFinderRequest";
import {defineInt} from "firebase-functions/params";


const SEAT_FINDER_RESERVATION_TIMEOUT_SEC = defineInt("SEAT_FINDER_RESERVATION_TIMEOUT_SEC");
const SEAT_FINDER_AWAY_TIMEOUT_SEC = defineInt("SEAT_FINDER_AWAY_TIMEOUT_SEC");

export default class SeatFinderHandler {
    private readonly sessionHandler: SessionHandler;
    private readonly seatHandler: SeatHandler;
    private stateChangesRef: database.Reference;
    private userHistoryRef: database.Reference;

    constructor(
        readonly userId: string,
    ) {
        this.sessionHandler = new SessionHandler(this.userId);
        this.seatHandler = new SeatHandler(this.userId);

        const seatFinderRef = DatabaseUtil.Instance.seatFinderRef();
        this.stateChangesRef = seatFinderRef.child(userId).child(REFERENCE_STATE_CHANGES_NAME);
        this.userHistoryRef = seatFinderRef.child(userId).child(REFERENCE_HISTORY_NAME);
    }

    handleSeatFinderRequest(
        requestType: SeatFinderRequestType,
        current: number,
        endTime: number | null,
        seatPosition: SeatPosition | null = null,
        reason: SeatFinderEventBy,
    ) {
        logger.debug("[SeatFinderHandler] handleSeatFinderRequest", {requestType, endTime, seatPosition, reason});

        return Promise.resolve().then(() => {
            switch (requestType) {
                case SeatFinderRequestType.ReserveSeat: {
                    if (!seatPosition) {
                        throw new https.HttpsError("invalid-argument", "Seat position is required");
                    }
                    return this.reserveSeat(seatPosition, current);
                }
                case SeatFinderRequestType.OccupySeat: {
                    return this.occupySeat(current, endTime);
                }
                case SeatFinderRequestType.LeaveAway: {
                    return this.leaveAway(current);
                }
                case SeatFinderRequestType.DoBusiness: {
                    return this.doBusiness(current, endTime);
                }
                case SeatFinderRequestType.ResumeUsing: {
                    return this.resumeUsing();
                }
                case SeatFinderRequestType.ChangeMainStateEndTime: {
                    return this.changeMainStateEndTime(current, endTime);
                }
                case SeatFinderRequestType.ChangeSubStateEndTime: {
                    return this.changeSubStateEndTime(current, endTime);
                }
                case SeatFinderRequestType.Quit: {
                    return this.quit();
                }
                default: {
                    logger.error("Unknown request type", {requestType});
                    throw new https.HttpsError("unimplemented", `Unknown request type : ${requestType}`);
                }
            }
        }).then((result: SeatFinderResult) => {
            return this.addStateChange(requestType, current, reason, result.sessionResult, result.seatResult)
                .then(() => {
                    if (requestType === SeatFinderRequestType.Quit) {
                        if (result.sessionResult?.before) {
                            return this.cleanupSession(current, result.sessionResult?.before);
                        }
                    }
                    return Promise.resolve();
                }).then(() => {
                    return {
                        sessionResult: result.sessionResult ? {
                            ...result.sessionResult,
                            rollback: undefined,
                        } : null,
                        seatResult: result.seatResult ? {
                            ...result.seatResult,
                            rollback: undefined,
                        } : null,
                    };
                });
        });
    }

    async reserveSeat(
        seatPosition: SeatPosition,
        startTime: number,
    ) {
        logger.debug("[SeatFinderHandler] createSession");
        const sessionId = this.newSessionKey();
        const endTime = getEndTime(startTime, SEAT_FINDER_RESERVATION_TIMEOUT_SEC.value());
        return this.seatHandler.reserveSeat(seatPosition, endTime)
            .then((seatResult) => {
                if (seatResult.resultCode !== ResultCode.OK) {
                    return <SeatFinderResult>{seatResult};
                }
                logger.debug("[SeatFinderHandler] handleSeat success", {seatResult});
                return this.sessionHandler.reserveSeat(sessionId, seatPosition, startTime, endTime)
                    .then(async (sessionResult) => {
                        if (sessionResult.resultCode !== ResultCode.OK) {
                            await seatResult.rollback();
                        }
                        logger.debug("[SeatFinderHandler] handleSession success", {sessionResult});
                        return <SeatFinderResult>{sessionResult, seatResult};
                    });
            });
    }

    async occupySeat(
        startTime: number,
        endTime: number | null = null,
    ) {
        return this.handleSessionAndSeat(
            () => this.sessionHandler.occupySeat(startTime, endTime),
            (currentSession: CurrentSession) => this.seatHandler.occupySeat(currentSession.seatPosition, endTime),
        );
    }

    async leaveAway(
        startTime: number,
    ) {
        const endTime = getEndTime(startTime, SEAT_FINDER_AWAY_TIMEOUT_SEC.value());
        return this.handleSessionAndSeat(
            () => this.sessionHandler.leaveAway(startTime, endTime),
            (currentSession: CurrentSession) => this.seatHandler.away(currentSession.seatPosition),
        );
    }

    async doBusiness(
        startTime: number,
        endTime: number | null = null,
    ) {
        return this.handleSessionAndSeat(
            () => this.sessionHandler.doBusiness(startTime, endTime),
            (currentSession: CurrentSession) => this.seatHandler.away(currentSession.seatPosition),
        );
    }

    async resumeUsing() {
        return this.handleSessionAndSeat(
            () => this.sessionHandler.resumeUsing(),
            (currentSession: CurrentSession) => this.seatHandler.resumeUsing(currentSession.seatPosition),
        );
    }

    async changeMainStateEndTime(
        startTime: number,
        endTime: number | null = null,
    ) {
        return this.handleSessionAndSeat(
            () => this.sessionHandler.changeMainStateEndTime(startTime, endTime),
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

    async changeSubStateEndTime(
        startTime: number,
        endTime: number | null = null,
    ) {
        return this.handleSessionAndSeat(
            () => this.sessionHandler.changeSubStateEndTime(startTime, endTime),
        );
    }

    async quit() {
        logger.debug("[SeatFinderHandler] quit");
        return this.sessionHandler.quit()
            .then(async (sessionResult) => {
                if (sessionResult.resultCode !== ResultCode.OK) {
                    return <SeatFinderResult>{sessionResult};
                }
                if (!sessionResult.before) {
                    return <SeatFinderResult>{
                        sessionResult: {
                            ...sessionResult,
                            resultCode: ResultCode.INVALID_SESSION_STATE,
                        },
                    };
                }
                return this.seatHandler.freeSeat(sessionResult.before.seatPosition)
                    .then(async (seatResult) => {
                        if (seatResult.resultCode !== ResultCode.OK) {
                            await sessionResult.rollback();
                        }
                        logger.debug("[SeatFinderHandler] handleSeat success", {seatResult});
                        return <SeatFinderResult>{sessionResult, seatResult};
                    });
            });
    }

    private handleSessionAndSeat(
        handleSession: () => Promise<TransactionResult<CurrentSession>>,
        handleSeat?: (currentSession: CurrentSession) => Promise<TransactionResult<Seat>>,
    ) {
        logger.debug("[SeatFinderHandler] transaction");
        return handleSession()
            .then(async (sessionResult) => {
                if (sessionResult.resultCode !== ResultCode.OK) {
                    return <SeatFinderResult>{sessionResult};
                }
                if (!sessionResult.after) {
                    return <SeatFinderResult>{
                        sessionResult: {
                            ...sessionResult,
                            resultCode: ResultCode.INVALID_SESSION_STATE,
                        },
                    };
                }
                logger.debug("[SeatFinderHandler] handleSession success", {sessionResult});
                if (handleSeat) {
                    return handleSeat(sessionResult.after)
                        .then(async (seatResult) => {
                            if (seatResult.resultCode !== ResultCode.OK) {
                                await sessionResult.rollback();
                            }
                            logger.debug("[SeatFinderHandler] handleSeat success", {seatResult});
                            return <SeatFinderResult>{sessionResult, seatResult};
                        });
                }
                return <SeatFinderResult>{sessionResult};
            });
    }

    private newSessionKey() {
        const sessionId = this.stateChangesRef.push().key;
        if (!sessionId) throw new https.HttpsError("unknown", "You are pushing on root");
        logger.debug("[SeatFinderHandler] Creating new session", {sessionId});
        return sessionId;
    }

    private addStateChange(
        requestType: SeatFinderRequestType,
        current: number,
        reason: SeatFinderEventBy,
        sessionResult?: TransactionResult<CurrentSession>,
        seatResult?: TransactionResult<Seat>,
    ) {
        const success = (sessionResult ? sessionResult.resultCode === ResultCode.OK : false) &&
            (seatResult ? seatResult.resultCode === ResultCode.OK : true);
        const sessionId = sessionResult?.after?.sessionId ?? sessionResult?.before?.sessionId;
        if (!sessionId) return Promise.resolve();
        return this.stateChangesRef.child(sessionId).push(<SeatFinderEvent>{
            requestType: requestType,
            timestamp: current,
            reason: reason,
            resultUserState: sessionResult ? sessionResult.after?.subState?.state ?? sessionResult.after?.mainState?.state ?? "User_None" : null,
            resultSeatState: seatResult?.after?.state ?? null,
            success,
        }).then(async () => {
            if (!success) {
                await this.sessionHandler.hasFailed();
            }
        });
    }

    private cleanupSession(current: number, currentSession: CurrentSession) {
        logger.debug("[SeatFinderHandler] cleanupSession", {currentSession});
        return this.userHistoryRef.child(currentSession.sessionId).set(
            <PreviousSession>{
                startTime: currentSession.startSessionTime,
                endTime: current,
                seatPosition: currentSession.seatPosition,
                hasFailure: currentSession.hasFailure,
            }
        );
    }

    clearAllRecords() {
        return DatabaseUtil.Instance.seatFinderRef().child(this.userId).remove().then(() => {
            return DatabaseUtil.Instance.usersRef().child(this.userId).remove();
        });
    }
}
