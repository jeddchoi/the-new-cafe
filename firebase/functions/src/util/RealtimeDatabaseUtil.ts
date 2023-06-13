import {Database, getDatabase} from "firebase-admin/database";

const REFERENCE_USER_STATE_NAME = "user_state";
const REFERENCE_USER_SESSION_NAME = "user_session";
const REFERENCE_USER_HISTORY_NAME = "user_history";


export default class RealtimeDatabaseUtil {
    static db: Database = getDatabase();

    static getUserState(userId: string) {
        return this.db.ref(REFERENCE_USER_STATE_NAME).child(userId);
    }

    static getUserSessionRef(userId: string) {
        return this.db.ref(REFERENCE_USER_SESSION_NAME).child(userId);
    }

    static getUserHistoryRef(userId: string) {
        return this.db.ref(REFERENCE_USER_HISTORY_NAME).child(userId);
    }

    static deletePath(path: string) {
        return this.db.ref(path).remove();
    }
}

export {
    REFERENCE_USER_STATE_NAME,
    REFERENCE_USER_SESSION_NAME,
    REFERENCE_USER_HISTORY_NAME,
};
