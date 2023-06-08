import RealtimeDatabaseUtil from "../util/RealtimeDatabaseUtil";
import {UserSession} from "../model/UserSession";

export default class UserSessionHandler {
    // TODO: need to verify
    static getUserSessionData(userId: string): Promise<UserSession> {
        return RealtimeDatabaseUtil.getUserSession(userId).once("value").then((snapshot) => {
            return snapshot.val() as UserSession;
        });
    }
}
