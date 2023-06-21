import {databaseUtil, DatabaseUtil} from "../DatabaseUtil";
import {SeatPosition} from "../../_firestore/model/SeatPosition";
import {logger} from "firebase-functions/v2";
import {ResultCode} from "../../seat-finder/_enum/ResultCode";
import {CurrentSession} from "../model/CurrentSession";
import {database} from "firebase-admin";
import {PartialUserState} from "../model/PartialUserState";
import {UserStateType} from "../../seat-finder/_enum/UserStateType";
import {TimerInfo} from "../model/TimerInfo";
import {SeatFinderRequestType} from "../../seat-finder/_enum/SeatFinderRequestType";
import {https} from "firebase-functions/lib/v2";

const REFERENCE_CURRENT_SESSION_NAME = "session";

const REFERENCE_HISTORY_NAME = "history";

export default class SessionHandler {
    private readonly databaseUtil: DatabaseUtil;
    private userCurrentSessionRef: database.Reference;
    private userHistoryRef: database.Reference;

    private userCurrentSessionPath: string;

    constructor(
        private readonly userId: string,
    ) {
        this.databaseUtil = databaseUtil ?? new DatabaseUtil();
        const seatFinderRef = this.databaseUtil.seatFinderRef();
        this.userCurrentSessionRef = seatFinderRef.child(REFERENCE_CURRENT_SESSION_NAME).child(userId);
        const key = this.userCurrentSessionRef.key;
        if (!key) throw new https.HttpsError("unknown", "You are pushing on root");
        this.userCurrentSessionPath = key;
        this.userHistoryRef = seatFinderRef.child(REFERENCE_HISTORY_NAME).child(userId);
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
                mainState: <PartialUserState>{
                    startTime,
                    state: UserStateType.Reserved,
                    timer: endTime === null ? null : <TimerInfo>{
                        willRequestType: SeatFinderRequestType.Quit,
                        endTime,
                        taskId: this.getTimerTaskId(SeatFinderRequestType.Quit, endTime),
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
                logger.error("[SessionHandler] Session not found", {existing});
                throw new https.HttpsError("not-found", "Session not found", {existing});
            }
            if (existing.mainState.state !== UserStateType.Reserved) {
                logger.error("[SessionHandler] Session is in invalid state", {existing});
                throw ResultCode.INVALID_SESSION_STATE;
            }

            return <CurrentSession>{
                ...existing,
                mainState: <PartialUserState>{
                    startTime,
                    state: UserStateType.Occupied,
                    timer: endTime === null ? null : <TimerInfo>{
                        willRequestType: SeatFinderRequestType.Quit,
                        endTime,
                        taskId: this.getTimerTaskId(SeatFinderRequestType.Quit, endTime),
                    },
                },
            };
        });
    };

    private getTimerTaskId(willRequestType: SeatFinderRequestType, endTime: number) {
        return `${this.userId}-${willRequestType}-${endTime}`;
    }
}
