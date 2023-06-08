import {Database, getDatabase, Reference} from "firebase-admin/database";

const REFERENCE_USER_STATE_NAME = "user_state";
const REFERENCE_USER_SESSION_NAME = "user_session";
const REFERENCE_USER_HISTORY_NAME = "user_history";


export default class RealtimeDatabaseUtil {
    static db: Database = getDatabase();

    static getUserState(userId: string): Reference {
        return this.db.ref(REFERENCE_USER_STATE_NAME).child(userId);
    }

    static getUserSession(userId: string): Reference {
        return this.db.ref(REFERENCE_USER_SESSION_NAME).child(userId);
    }
    static getUserHistory(userId: string): Reference {
        return this.db.ref(REFERENCE_USER_HISTORY_NAME).child(userId);
    }
}


