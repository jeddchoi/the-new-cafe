import {DatabaseUtil} from "./DatabaseUtil";
import {SeatPosition} from "../_firestore/model/SeatPosition";
import {logger} from "firebase-functions/v2";
import {ResultCode} from "../seat-finder/_enum/ResultCode";
import {CurrentSession} from "./model/CurrentSession";
import {database} from "firebase-admin";
import {UserMainStateType} from "../seat-finder/_enum/UserMainStateType";
import {TimerInfo} from "./model/TimerInfo";
import {SeatFinderRequestType} from "../seat-finder/_enum/SeatFinderRequestType";
import {UserSubStateType} from "../seat-finder/_enum/UserSubStateType";
import {UserStateType} from "../seat-finder/_enum/UserStateType";

const REFERENCE_CURRENT_SESSION_NAME = "session";
const REFERENCE_SESSION_HAS_FAILURE_NAME = "hasFailure";

let databaseUtil: DatabaseUtil;

export default class SessionHandler {
    private readonly userCurrentSessionRef: database.Reference;
    private readonly userCurrentSessionPath: string;

    constructor(
        private readonly userId: string,
    ) {
        databaseUtil = databaseUtil ?? new DatabaseUtil();
        const seatFinderRef = databaseUtil.seatFinderRef();
        this.userCurrentSessionRef = seatFinderRef.child(REFERENCE_CURRENT_SESSION_NAME).child(userId);
        this.userCurrentSessionPath = databaseUtil.getRefPath(this.userCurrentSessionRef);
    }

    hasFailed() {
        return databaseUtil.transaction<boolean>(
            `${this.userCurrentSessionPath}/${REFERENCE_SESSION_HAS_FAILURE_NAME}`,
            () => {
                return true;
            }
        );
    }

    reserveSeat = (sessionId: string, seatPosition: SeatPosition, startTime: number, endTime: number | null = null) => {
        logger.debug("[SessionHandler] reserveSeat", {
            seatPosition,
            startTime,
            endTime: endTime !== null ? new Date(endTime).toLocaleTimeString() : "no deadline",
        });
        return databaseUtil.transaction<CurrentSession>(this.userCurrentSessionPath, (existing) => {
            if (existing) {
                logger.warn("[SessionHandler] Session already exists", {existing});
                throw ResultCode.ALREADY_IN_PROGRESS;
            }

            return <CurrentSession>{
                sessionId,
                seatPosition,
                startSessionTime: startTime,
                hasFailure: false,
                mainState: {
                    startTime,
                    state: UserMainStateType.Reserved,
                    timer: endTime === null ? null : <TimerInfo>{
                        willRequestType: SeatFinderRequestType.Quit,
                        endTime,
                        taskId: this.getTimerTaskId(UserMainStateType.Reserved, endTime),
                    },
                },
            };
        });
    };

    occupySeat = (startTime: number, endTime: number | null = null) => {
        logger.debug("[SessionHandler] occupySeat", {
            startTime,
            endTime: endTime !== null ? new Date(endTime).toLocaleTimeString() : "no deadline",
        });
        return databaseUtil.transaction<CurrentSession>(this.userCurrentSessionPath, (existing) => {
            if (!existing) {
                logger.warn("[SessionHandler] Session not found", {existing});
                return null;
            }
            if (existing.mainState.state !== UserMainStateType.Reserved) {
                logger.error("[SessionHandler] Session is in invalid state", {existing});
                throw ResultCode.INVALID_SESSION_STATE;
            }

            return <CurrentSession>{
                ...existing,
                mainState: {
                    startTime,
                    state: UserMainStateType.Occupied,
                    timer: endTime === null ? null : <TimerInfo>{
                        willRequestType: SeatFinderRequestType.Quit,
                        endTime,
                        taskId: this.getTimerTaskId(UserMainStateType.Occupied, endTime),
                    },
                },
            };
        });
    };

    leaveAway = (startTime: number, endTime: number | null = null) => {
        logger.debug("[SessionHandler] leaveAway", {
            startTime,
            endTime: endTime !== null ? new Date(endTime).toLocaleTimeString() : "no deadline",
        });
        return databaseUtil.transaction<CurrentSession>(this.userCurrentSessionPath, (existing) => {
            if (!existing) {
                logger.warn("[SessionHandler] Session not found", {existing});
                return null;
            }
            if (existing.mainState.state !== UserMainStateType.Occupied) {
                logger.error("[SessionHandler] Session is in invalid state", {existing});
                throw ResultCode.INVALID_SESSION_STATE;
            }

            return <CurrentSession>{
                ...existing,
                subState: {
                    startTime,
                    state: UserSubStateType.Away,
                    timer: endTime === null ? null : <TimerInfo>{
                        willRequestType: SeatFinderRequestType.Quit,
                        endTime,
                        taskId: this.getTimerTaskId(UserSubStateType.Away, endTime),
                    },
                },
            };
        });
    };

    doBusiness = (startTime: number, endTime: number | null = null) => {
        logger.debug("[SessionHandler] doBusiness", {
            startTime,
            endTime: endTime !== null ? new Date(endTime).toLocaleTimeString() : "no deadline",
        });
        return databaseUtil.transaction<CurrentSession>(this.userCurrentSessionPath, (existing) => {
            if (!existing) {
                logger.warn("[SessionHandler] Session not found", {existing});
                return null;
            }
            if (existing.mainState.state !== UserMainStateType.Occupied) {
                logger.error("[SessionHandler] Session is in invalid state", {existing});
                throw ResultCode.INVALID_SESSION_STATE;
            }

            return <CurrentSession>{
                ...existing,
                subState: {
                    startTime,
                    state: UserSubStateType.OnBusiness,
                    timer: endTime === null ? null : <TimerInfo>{
                        willRequestType: SeatFinderRequestType.ResumeUsing,
                        endTime,
                        taskId: this.getTimerTaskId(UserSubStateType.OnBusiness, endTime),
                    },
                },
            };
        });
    };

    resumeUsing = () => {
        logger.debug("[SessionHandler] resumeUsing");
        return databaseUtil.transaction<CurrentSession>(this.userCurrentSessionPath, (existing) => {
            if (!existing) {
                logger.warn("[SessionHandler] Session not found", {existing});
                return null;
            }

            return <CurrentSession>{
                ...existing,
                subState: null,
            };
        });
    };

    changeMainStateEndTime = (startTime: number, endTime: number | null = null) => {
        logger.debug("[SessionHandler] changeMainStateEndTime", {
            startTime,
            endTime: endTime !== null ? new Date(endTime).toLocaleTimeString() : "no deadline",
        });
        return databaseUtil.transaction<CurrentSession>(this.userCurrentSessionPath, (existing) => {
            if (!existing) {
                logger.warn("[SessionHandler] Session not found", {existing});
                return null;
            }

            return <CurrentSession>{
                ...existing,
                mainState: {
                    ...existing.mainState,
                    timer: endTime === null ? null : <TimerInfo>{
                        ...existing.mainState.timer,
                        endTime,
                        taskId: this.getTimerTaskId(existing.mainState.state, endTime),
                    },
                },
            };
        });
    };

    changeSubStateEndTime = (startTime: number, endTime: number | null = null) => {
        logger.debug("[SessionHandler] changeSubStateEndTime", {
            startTime,
            endTime: endTime !== null ? new Date(endTime).toLocaleTimeString() : "no deadline",
        });
        return databaseUtil.transaction<CurrentSession>(this.userCurrentSessionPath, (existing) => {
            if (!existing) {
                logger.warn("[SessionHandler] Session not found", {existing});
                return null;
            }

            if (!existing.subState) {
                logger.error("[SessionHandler] Session is in invalid state", {existing});
                throw ResultCode.INVALID_SESSION_STATE;
            }

            return <CurrentSession>{
                ...existing,
                subState: {
                    ...existing.subState,
                    timer: endTime === null ? null : <TimerInfo>{
                        ...existing.subState.timer,
                        endTime,
                        taskId: this.getTimerTaskId(existing.subState.state, endTime),
                    },
                },
            };
        });
    };

    quit = () => {
        logger.debug("[SessionHandler] quit");
        return databaseUtil.transaction<CurrentSession>(this.userCurrentSessionPath, () => {
            return null;
        });
    };

    private getTimerTaskId(fromState: UserStateType, endTime: number) {
        return `${this.userId}-${fromState}-${endTime}`;
    }
}