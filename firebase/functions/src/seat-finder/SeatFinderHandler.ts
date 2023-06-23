import {databaseUtil, DatabaseUtil} from "../_database/DatabaseUtil";
import {database} from "firebase-admin";
import {https, logger} from "firebase-functions/v2";
import {TransactionResult} from "../helper/TransactionResult";
import {SeatFinderEvent} from "../_database/model/SeatFinderEvent";
import {CurrentSession} from "../_database/model/CurrentSession";
import {Seat} from "../_firestore/model/Seat";
import {SeatFinderRequestType} from "./_enum/SeatFinderRequestType";
import SessionHandler from "../_database/handler/SessionHandler";
import SeatHandler from "../_firestore/handler/SeatHandler";
import {ResultCode} from "./_enum/ResultCode";
import {UserMainStateType} from "./_enum/UserMainStateType";
import {SeatPosition} from "../_firestore/model/SeatPosition";
import {SeatFinderEventBy} from "./_enum/SeatFinderEventBy";
import {PreviousSession} from "../_database/model/PreviousSession";
import {getEndTime, ISeatFinderRequest} from "./_model/SeatFinderRequest";
import {SeatFinderResult} from "./_model/SeatFinderResult";

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

    handleSeatFinderRequest(request: ISeatFinderRequest, isTimeout = false) {
        logger.debug("[SeatFinderHandler] handleSeatFinderRequest", {request, isTimeout});
        const current = new Date().getTime();
        const endTime = getEndTime(request, current);
        return Promise.resolve().then(() => {
            switch (request.requestType) {
                case SeatFinderRequestType.ReserveSeat: {
                    if (!request.seatPosition) {
                        throw new https.HttpsError("invalid-argument", "Seat position is required");
                    }
                    return this.reserveSeat(request.seatPosition, current, endTime);
                }
                case SeatFinderRequestType.OccupySeat: {
                    return this.occupySeat(current, endTime);
                }
                case SeatFinderRequestType.LeaveAway: {
                    return this.leaveAway(current, endTime);
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
                    logger.error("Unknown request type", {request});
                    throw new https.HttpsError("unimplemented", `Unknown request type : ${JSON.stringify(request)}`);
                }
            }
        }).then((result: SeatFinderResult) => {
            return this.addStateChange(request.requestType, current, isTimeout, result.sessionResult, result.seatResult)
                .then(() => {
                    if (request.requestType === SeatFinderRequestType.Quit) {
                        if (result.sessionResult?.before) {
                            return this.cleanupSession(current, result.sessionResult?.before);
                        }
                    }
                    return Promise.resolve();
                }).then(() => {
                    return result;
                });
        });
    }

    async reserveSeat(
        seatPosition: SeatPosition,
        startTime: number,
        endTime: number | null = null,
    ) {
        logger.debug("[SeatFinderHandler] createSession");
        const sessionId = this.newSessionKey();

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
        endTime: number | null = null,
    ) {
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
                    throw ResultCode.INVALID_SESSION_STATE;
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
                    throw ResultCode.INVALID_SESSION_STATE;
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
        isTimeout: boolean,
        sessionResult?: TransactionResult<CurrentSession>,
        seatResult?: TransactionResult<Seat>,
    ) {
        const success = (sessionResult ? sessionResult.resultCode === ResultCode.OK : false) &&
            (seatResult ? seatResult.resultCode === ResultCode.OK : true);
        const sessionId = sessionResult?.after?.sessionId ?? sessionResult?.before?.sessionId;
        if (!sessionId) throw ResultCode.INVALID_SESSION_STATE;
        return this.stateChangesRef.child(sessionId).push(<SeatFinderEvent>{
            requestType: requestType,
            timestamp: current,
            reason: isTimeout ? SeatFinderEventBy.Timeout : SeatFinderEventBy.UserAction,
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
}
