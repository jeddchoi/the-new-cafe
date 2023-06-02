import {Database, DataSnapshot, getDatabase, Reference} from "firebase-admin/database";

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

    static getUserHistory(userId: string): Reference {
        return this.db.ref(REFERENCE_USER_HISTORY_NAME).child(userId);
    }
}


