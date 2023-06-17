import {IUserStateExternal, OverallState, SeatPosition, TimerInfo, UserState, UserStatus} from "../model/UserState";
import RealtimeDatabaseUtil from "../util/RealtimeDatabaseUtil";
import {UserStateType} from "../model/UserStateType";
import {UserStateChangeReason} from "../model/UserStateChangeReason";
import {serializeSeatId} from "../model/SeatPosition";
import {logger} from "firebase-functions/v2";
import {RequestType} from "../model/RequestType";
import {BusinessResultCode} from "../model/BusinessResultCode";


const REFERENCE_USER_STATUS_NAME = "status";
const REFERENCE_USER_OVERALL_STATE_NAME = "overall";
const REFERENCE_USER_TEMPORARY_STATE_NAME = "temporary";

export default class UserStateHandler {
    static getUserStateData(userId: string) {
        logger.debug(`[UserStateHandler] getUserStateData(${userId})`);
        return RealtimeDatabaseUtil.getUserState(userId).once("value").then((snapshot) => {
            const val = snapshot.val() as IUserStateExternal;
            return UserState.fromExternal(userId, val);
        });
    }

    static reserveSeat(userId: string, seatPosition: SeatPosition, startTime: number, endTime: number | null) {
        logger.debug(`[UserStateHandler] reserveSeat(${userId}, ${JSON.stringify(seatPosition)}, ${startTime}, ${endTime})`);
        return this.transactionOnStatus(userId, (existing) => {
            if (existing.overall || existing.temporary) throw BusinessResultCode.ALREADY_IN_PROGRESS;
            return <UserStatus>{
                overall: <OverallState>{
                    state: UserStateType.Reserved,
                    reason: UserStateChangeReason.UserAction,
                    startTime,
                    timer: endTime === null ? null : <TimerInfo>{
                        endTime,
                        taskName: this.getTaskName(userId, UserStateType.Reserved, endTime),
                        willRequestType: RequestType.Quit,
                    },
                    seatPosition: serializeSeatId(seatPosition),
                },
            };
        });
    }

    static occupySeat(userId: string, startTime: number, endTime: number | null) {
        logger.debug(`[UserStateHandler] occupySeat(${userId}, ${startTime}, ${endTime})`);

        return this.transactionOnStatus(userId, (existing) => {
            if (existing.overall.state !== UserStateType.Reserved) throw BusinessResultCode.INVALID_STATE;

            return <UserStatus>{
                ...existing,
                overall: {
                    ...existing.overall,
                    state: UserStateType.Occupied,
                    reason: UserStateChangeReason.UserAction,
                    startTime,
                    timer: endTime === null ? null : <TimerInfo>{
                        endTime,
                        taskName: this.getTaskName(userId, UserStateType.Occupied, endTime),
                        willRequestType: RequestType.Quit,
                    },
                },
            };
        });
    }

    static quit(userId: string) {
        logger.debug(`[UserStateHandler] quit(${userId})`);
        return this.transactionOnStatus(userId, (existing) => {
            return null;
        });
    }

    static updateUserTemporaryStateInSession(userId: string, state: UserStateType, startTime: number, endTime: number | null) {
        logger.debug(`[UserStateHandler] updateUserTemporaryStateInSession(${userId}, ${state}, ${startTime}, ${endTime})`);

        return this.transactionOnStatus(userId, (existing) => {
            return <UserStatus>{
                ...existing,
                temporary: {
                    state,
                    reason: UserStateChangeReason.UserAction,
                    startTime,
                    timer: endTime === null ? null : <TimerInfo>{
                        endTime,
                        taskName: this.getTaskName(userId, state, endTime),
                        willRequestType: state === UserStateType.Away ? RequestType.Quit : RequestType.ResumeUsing,
                    },
                },
            };
        });
    }

    static removeTemporaryState(userId: string) {
        logger.debug(`[UserStateHandler] removeTemporaryState(${userId})`);
        return this.transactionOnStatus(userId, (existing) => {
            return <UserStatus> {
                ...existing,
                temporary: null,
            };
        });
    }


    static updateOverallTimer(userId: string, newEndTime: number | null, current: number) {
        logger.debug(`[UserStateHandler] updateOverallTimer(${userId}, ${newEndTime})`);
        return this.transactionOnStatus(userId, (existing) => {
            if (!existing.overall) throw BusinessResultCode.TIMER_CHANGE_NOT_AVAILABLE;
            if (existing.overall.timer && existing.overall.timer.endTime <= current) throw BusinessResultCode.ALREADY_TIMEOUT;
            return {
                ...existing,
                overall: {
                    ...existing.overall,
                    timer: newEndTime === null ? null : <TimerInfo>{
                        ...existing.overall.timer,
                        endTime: newEndTime,
                        taskName: this.getTaskName(userId, existing.overall.state, newEndTime),
                    },
                },
            };
        });
    }

    static updateTemporaryTimer(userId: string, newEndTime: number | null, current: number) {
        logger.debug(`[UserStateHandler] updateTemporaryTimer(${userId}, ${newEndTime})`);
        return this.transactionOnStatus(userId, (existing) => {
            if (!existing.temporary) throw BusinessResultCode.TIMER_CHANGE_NOT_AVAILABLE;
            if (existing.overall.timer && existing.overall.timer.endTime <= current) throw BusinessResultCode.ALREADY_TIMEOUT;
            if (newEndTime && existing.overall.timer && existing.overall.timer.endTime <= newEndTime) throw BusinessResultCode.TEMPORARY_LONGER_THAN_OVERALL;
            if (existing.temporary.timer && existing.temporary.timer.endTime <= current) throw BusinessResultCode.ALREADY_TIMEOUT;

            return <UserStatus>{
                ...existing,
                temporary: {
                    ...existing.temporary,
                    timer: newEndTime === null ? null : <TimerInfo>{
                        ...existing.temporary?.timer,
                        endTime: newEndTime,
                        taskName: this.getTaskName(userId, existing.temporary?.state, newEndTime),
                    },
                },
            };
        });
    }

    private static transactionOnStatus(userId: string, checkAndUpdate: (existing: UserStatus) => UserStatus | null) {
        return RealtimeDatabaseUtil.runTransactionOnRef(this.getUserStatusRef(userId), checkAndUpdate);
    }

    private static getUserStatusRef(userId: string) {
        return RealtimeDatabaseUtil.getUserState(userId).child(REFERENCE_USER_STATUS_NAME);
    }

    private static getUserOverallStateRef(userId: string) {
        return RealtimeDatabaseUtil.getUserState(userId).child(REFERENCE_USER_STATUS_NAME).child(REFERENCE_USER_OVERALL_STATE_NAME);
    }

    private static getUserTemporaryStateRef(userId: string) {
        return RealtimeDatabaseUtil.getUserState(userId).child(REFERENCE_USER_STATUS_NAME).child(REFERENCE_USER_TEMPORARY_STATE_NAME);
    }

    private static getTaskName(userId: string, state: UserStateType, endTime: number) {
        return `${userId}_${UserStateType[state]}_${endTime}`;
    }
}
