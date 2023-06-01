import {logger} from "firebase-functions/v2";
import {getDatabase, DataSnapshot, Database, Reference} from "firebase-admin/database";
import {throwFunctionsHttpsError} from "./functions_helper";
import {ITimerTask, IUserStatusExternal, UserStatus} from "../model/UserStatus";
import {IUserStatusChangeExternal, UserStatusChange} from "../model/UserStatusChange";
import {UserStatusChangeReason} from "../model/UserStatusChangeReason";


export const REFERENCE_USER_STATUS_NAME = "user_status";
export const REFERENCE_USER_HISTORY_NAME = "user_history";

export type TransactionResult = {
    committed: boolean;
    snapshot: DataSnapshot;
};

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
        // return this.getUserStatus(userId).transaction(transactionUpdate, (error, committed) => {
        //     if (error) {
        //         throwFunctionsHttpsError("internal", `[${userId}] Error updating user status`);
        //     } else if (!committed) {
        //         logger.warn(`[${userId}] Not committed updating user status`);
        //     } else {
        //         logger.info(`[${userId}] User updated successfully!`);
        //     }
        // }, true);
    }

    static updateUserTimerTask(userId: string, timer: "currentTimer" | "usageTimer", timerTaskInfo: ITimerTask) {
        return this.getUserStatus(userId).child(timer).update(timerTaskInfo);
    }

    static removeUserTimerTask(userId: string, timer: "currentTimer" | "usageTimer") {
        return this.getUserStatus(userId).child(timer).remove();
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


