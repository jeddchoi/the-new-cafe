import RealtimeDatabaseUtil from "../util/RealtimeDatabaseUtil";
import {CompletedUserSession, START_TIME_PROPERTY_NAME} from "../model/UserSession";

export default class UserHistoryHandler {
    // TODO: need to verify
    static getUserHistoryData(userId: string, perPage: number, startAfter?: Date) {
        let query = RealtimeDatabaseUtil.getUserHistoryRef(userId).orderByChild(START_TIME_PROPERTY_NAME);
        if (startAfter) {
            query = query.startAfter(startAfter.getTime().toString());
        }
        return query.limitToFirst(perPage).once("value").then((snapshot) => {
            const result: CompletedUserSession[] = [];
            snapshot.forEach((data) => {
                result.push(data.val() as CompletedUserSession);
            });
            return result;
        });
    }

    // TODO: need to verify
    static getLastUserHistoryData(userId: string) {
        return RealtimeDatabaseUtil.getUserHistoryRef(userId).orderByChild(START_TIME_PROPERTY_NAME).limitToLast(1).once("value").then((snapshot) => {
            return snapshot.val() as CompletedUserSession;
        });
    }
}
