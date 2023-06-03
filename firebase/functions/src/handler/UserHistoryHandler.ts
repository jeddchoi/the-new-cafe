import RealtimeDatabaseUtil from "../util/RealtimeDatabaseUtil";
import {throwFunctionsHttpsError} from "../util/functions_helper";
import {IUserStateChangeExternal, UserStateChange} from "../model/UserStateChange";

export default class UserHistoryHandler {
    // TODO: need to verify
    static getUserHistoryData(userId: string, perPage: number, startAfter?: Date): Promise<UserStateChange[]> {
        let query = RealtimeDatabaseUtil.getUserHistory(userId).orderByKey();
        if (startAfter) {
            query = query.startAfter(startAfter.getTime().toString());
        }
        return query.limitToFirst(perPage).once("value").then((snapshot) => {
            const result: UserStateChange[] = [];
            snapshot.forEach((data) => {
                result.push(UserStateChange.fromExternal(userId, parseInt(data.key ?? throwFunctionsHttpsError("not-found", "data key is null")), data.val() as IUserStateChangeExternal));
            });
            return result;
        });
    }

    // TODO: need to verify
    static getLastUserHistoryData(userId: string): Promise<UserStateChange> {
        return RealtimeDatabaseUtil.getUserHistory(userId).orderByKey().limitToLast(1).once("value").then((snapshot) => {
            const val = snapshot.val() as IUserStateChangeExternal;
            return UserStateChange.fromExternal(userId, parseInt(snapshot.key ?? throwFunctionsHttpsError("not-found", "data key is null")), val);
        });
    }
}
