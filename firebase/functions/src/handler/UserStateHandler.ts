import {IUserStateExternal, SeatPosition, UserState} from "../model/UserState";
import RealtimeDatabaseUtil from "../util/RealtimeDatabaseUtil";
import {UserStateType} from "../model/UserStateType";
import {UserStateChangeReason} from "../model/UserStateChangeReason";


export default class UserStateHandler {
    static getUserStateData(uid: string): Promise<UserState> {
        return RealtimeDatabaseUtil.getUserState(uid).once("value").then((snapshot) => {
            const val = snapshot.val() as IUserStateExternal;
            return UserState.fromExternal(uid, val);
        });
    }

    static reserveSeat(userId: string, seatPosition: SeatPosition, startTime: number, endTime: number | null): Promise<void> {
        return RealtimeDatabaseUtil.getUserState(userId).child("status/overall").update({
            state: UserStateType.Reserved,
            reason: UserStateChangeReason.UserAction,
            startTime,
            timer: endTime === null ? null : {
                endTime,
                taskName: this.getTaskName(userId, UserStateType.Reserved, startTime),
            },
            seatPosition,
        });
    }

    static occupySeat(userId: string, startTime: number, endTime: number | null) {
        return RealtimeDatabaseUtil.getUserState(userId).transaction((existing: IUserStateExternal | null) => {
            if (!existing) return;
            if (!existing.status) return;
            if (!existing.status.overall.seatPosition) return;
            return {
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
        return RealtimeDatabaseUtil.getUserState(userId).transaction((existing: IUserStateExternal | null) => {
            if (!existing) return;
            if (!existing.status) return;

            return {
                ...existing,
                status: null,
            };
        });
    }

    static removeTemporaryState(userId: string) {
        return RealtimeDatabaseUtil.getUserState(userId).transaction((existing: IUserStateExternal | null) => {
            if (!existing) return;
            if (!existing.status) return;
            return {
                ...existing,
                status: {
                    temporary: null,
                },
            };
        });
    }

    static updateUserTemporaryStateInSession(userId: string, state: UserStateType, startTime: number, endTime: number | null, isReset: boolean) {
        return RealtimeDatabaseUtil.getUserState(userId).transaction((existing: IUserStateExternal | null) => {
            if (!existing) return;
            if (!existing.status) return; // abort
            return {
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

    private static getTaskName(userId: string, state: UserStateType, startTime: number): string {
        return `${userId}_${UserStateType[state]}_${startTime}`;
    }


    // static updateUserStateData(userId: string, updateContent: { [key in keyof IUserStateExternal]?: IUserStateExternal[key] }): Promise<void> {
    //     return RealtimeDatabaseUtil.getUserState(userId).update(updateContent);
    // }
    //
    // static updateUserTimerTask(userId: string, taskType: TaskType.StartCurrentTimer | TaskType.StartUsageTimer, timerTaskInfo: TimerInfo) {
    //     if (taskType === TaskType.StartCurrentTimer) {
    //         return RealtimeDatabaseUtil.getUserState(userId).child(CURRENT_TIMER_PROPERTY_NAME).update(timerTaskInfo);
    //     } else {
    //         return RealtimeDatabaseUtil.getUserState(userId).child(USAGE_TIMER_PROPERTY_NAME).update(timerTaskInfo);
    //     }
    // }
    //
    // static removeUserTimerTask(userId: string, taskType: TaskType.StopCurrentTimer | TaskType.StopUsageTimer) {
    //     if (taskType === TaskType.StopCurrentTimer) {
    //         return RealtimeDatabaseUtil.getUserState(userId).child(CURRENT_TIMER_PROPERTY_NAME).remove();
    //     } else {
    //         return RealtimeDatabaseUtil.getUserState(userId).child(USAGE_TIMER_PROPERTY_NAME).remove();
    //     }
    // }
}
