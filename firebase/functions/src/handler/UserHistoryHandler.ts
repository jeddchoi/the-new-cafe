import RealtimeDatabaseUtil from "../util/RealtimeDatabaseUtil";
import {throwFunctionsHttpsError} from "../util/functions_helper";
import {IUserStatusChangeExternal, UserStatusChange} from "../model/UserStatusChange";

export default class UserHistoryHandler {
    // TODO: need to verify
    static getUserHistoryData(userId: string, perPage: number, startAfter?: Date): Promise<UserStatusChange[]> {
        let query = RealtimeDatabaseUtil.getUserHistory(userId).orderByKey();
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
        return RealtimeDatabaseUtil.getUserHistory(userId).orderByKey().limitToLast(1).once("value").then((snapshot) => {
            const val = snapshot.val() as IUserStatusChangeExternal;
            return UserStatusChange.fromExternal(userId, parseInt(snapshot.key ?? throwFunctionsHttpsError("not-found", "data key is null")), val);
        });
    }
}
