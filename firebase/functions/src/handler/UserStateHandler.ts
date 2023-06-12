import {IUserStateExternal, OverallState, SeatPosition, UserState} from "../model/UserState";
import RealtimeDatabaseUtil from "../util/RealtimeDatabaseUtil";
import {UserStateType} from "../model/UserStateType";
import {UserStateChangeReason} from "../model/UserStateChangeReason";
import {serializeSeatId} from "../model/SeatPosition";
import {logger} from "firebase-functions/v2";


export default class UserStateHandler {
    static getUserStateData(uid: string) {
        return RealtimeDatabaseUtil.getUserState(uid).once("value").then((snapshot) => {
            const val = snapshot.val() as IUserStateExternal;
            return UserState.fromExternal(uid, val);
        });
    }

    static reserveSeat(userId: string, seatPosition: SeatPosition, startTime: number, endTime: number | null) {
        logger.debug(`[UserStateHandler] reserveSeat(${userId}, ${seatPosition}, ${startTime}, ${endTime})`);
        return RealtimeDatabaseUtil.getUserState(userId).child("status/overall").update(<OverallState>{
            state: UserStateType.Reserved,
            reason: UserStateChangeReason.UserAction,
            startTime,
            timer: endTime === null ? null : {
                endTime,
                taskName: this.getTaskName(userId, UserStateType.Reserved, startTime),
            },
            seatPosition: serializeSeatId(seatPosition),
        });
    }

    static occupySeat(userId: string, startTime: number, endTime: number | null) {
        logger.debug(`[UserStateHandler] occupySeat(${userId}, ${startTime}, ${endTime})`);
        return RealtimeDatabaseUtil.getUserState(userId).transaction((existing: IUserStateExternal | null) => {
            if (!existing) return;
            if (!existing.status) return;
            if (!existing.status.overall.seatPosition) return;
            return <IUserStateExternal>{
                ...existing,
                status: {
                    overall: {
                        state: UserStateType.Occupied,
                        reason: UserStateChangeReason.UserAction,
                        startTime,
                        timer: endTime === null ? null : {
                            endTime,
                            taskName: this.getTaskName(userId, UserStateType.Occupied, startTime),
                        },
                    },
                },
            };
        });
    }

    static quit(userId: string) {
        logger.debug(`[UserStateHandler] quit(${userId})`);
        return RealtimeDatabaseUtil.getUserState(userId).transaction((existing: IUserStateExternal | null) => {
            if (!existing) return;
            if (!existing.status) return;

            return <IUserStateExternal>{
                ...existing,
                status: null,
            };
        });
    }

    static removeTemporaryState(userId: string) {
        logger.debug(`[UserStateHandler] removeTemporaryState(${userId})`);
        return RealtimeDatabaseUtil.getUserState(userId).transaction((existing: IUserStateExternal | null) => {
            if (!existing) return;
            if (!existing.status) return;
            return <IUserStateExternal>{
                ...existing,
                status: {
                    temporary: null,
                },
            };
        });
    }

    static updateUserTemporaryStateInSession(userId: string, state: UserStateType, startTime: number, endTime: number | null, isReset: boolean) {
        logger.debug(`[UserStateHandler] updateUserTemporaryStateInSession(${userId}, ${state}, ${startTime}, ${endTime}, ${isReset})`);
        return RealtimeDatabaseUtil.getUserState(userId).transaction((existing: IUserStateExternal | null) => {
            if (!existing) return;
            if (!existing.status) return;
            return <IUserStateExternal>{
                ...existing,
                status: {
                    temporary: {
                        state,
                        reason: UserStateChangeReason.UserAction,
                        startTime,
                        timer: endTime === null ? null : {
                            endTime,
                            taskName: this.getTaskName(userId, state, startTime),
                            isReset,
                        },
                    },
                },
            };
        });
    }

    private static getTaskName(userId: string, state: UserStateType, startTime: number) {
        return `${userId}_${UserStateType[state]}_${startTime}`;
    }
}
