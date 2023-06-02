import {Database, DataSnapshot, getDatabase, Reference} from "firebase-admin/database";
import {throwFunctionsHttpsError} from "./functions_helper";
import {ITimerTask, IUserStatusExternal, UserStatus} from "../model/UserStatus";
import {IUserStatusChangeExternal, UserStatusChange} from "../model/UserStatusChange";
import {TaskType} from "../model/TaskType";


export const REFERENCE_USER_STATUS_NAME = "user_status";
export const REFERENCE_USER_HISTORY_NAME = "user_history";

export type TransactionResult = {
    committed: boolean;
    snapshot: DataSnapshot;
};
const CURRENT_TIMER_PROPERTY_NAME = "currentTimer";
const USAGE_TIMER_PROPERTY_NAME = "usageTimer";

export default class RealtimeDatabaseUtil {
    static db: Database = getDatabase();

    static getUserStatus(uid: string): Reference {
        return this.db.ref(REFERENCE_USER_STATUS_NAME).child(uid);
    }

    static getUserStatusData(uid: string): Promise<UserStatus> {
        return this.getUserStatus(uid).once("value").then((snapshot) => {
            const val = snapshot.val() as IUserStatusExternal;
            return UserStatus.fromExternal(uid, val);
        });
    }

    static updateUserStatusData(userId: string, updateContent: { [key in keyof IUserStatusExternal]?: IUserStatusExternal[key] }): Promise<void> {
        return this.getUserStatus(userId).update(updateContent);
    }

    static updateUserTimerTask(userId: string, taskType: TaskType.StartCurrentTimer | TaskType.StartUsageTimer, timerTaskInfo: ITimerTask) {
        if (taskType === TaskType.StartCurrentTimer) {
            return this.getUserStatus(userId).child(CURRENT_TIMER_PROPERTY_NAME).update(timerTaskInfo);
        } else {
            return this.getUserStatus(userId).child(USAGE_TIMER_PROPERTY_NAME).update(timerTaskInfo);
        }
    }

    static removeUserTimerTask(userId: string, taskType: TaskType.StopCurrentTimer | TaskType.StopUsageTimer) {
        if (taskType === TaskType.StopCurrentTimer) {
            return this.getUserStatus(userId).child(CURRENT_TIMER_PROPERTY_NAME).remove();
        } else {
            return this.getUserStatus(userId).child(USAGE_TIMER_PROPERTY_NAME).remove();
        }
    }

    static getUserHistory(userId: string): Reference {
        return this.db.ref(REFERENCE_USER_HISTORY_NAME).child(userId);
    }

    // TODO: need to verify
    static getUserHistoryData(userId: string, perPage: number, startAfter?: Date): Promise<UserStatusChange[]> {
        let query = this.getUserHistory(userId).orderByKey();
        if (startAfter) {
            query = query.startAfter(startAfter.getTime().toString());
        }
        return query.limitToFirst(perPage).once("value").then((snapshot) => {
            const result: UserStatusChange[] = [];
            snapshot.forEach((data) => {
                result.push(UserStatusChange.fromExternal(userId, parseInt(data.key ?? throwFunctionsHttpsError("not-found", "data key is null")), data.val() as IUserStatusChangeExternal));
            });
            return result;
        });
    }

    // TODO: need to verify
    static getLastUserHistoryData(userId: string): Promise<UserStatusChange> {
        return this.getUserHistory(userId).orderByKey().limitToLast(1).once("value").then((snapshot) => {
            const val = snapshot.val() as IUserStatusChangeExternal;
            return UserStatusChange.fromExternal(userId, parseInt(snapshot.key ?? throwFunctionsHttpsError("not-found", "data key is null")), val);
        });
    }
}


