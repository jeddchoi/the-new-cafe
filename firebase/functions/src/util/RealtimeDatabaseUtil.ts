import {Database, getDatabase} from "firebase-admin/database";
import {TransactionResult} from "../model/TransactionResult";
import {database} from "firebase-admin";

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

    static deleteRef(pathOrRef: string | database.Reference) {
        return this.db.ref(pathOrRef).remove();
    }

    // in JSON world, absence of data is null, not undefined(undefined is not accepted)
    static runTransactionOnRef<T>(ref: database.Reference, checkAndUpdate: (existing: T) => T | null) {
        const result = new TransactionResult<T>();
        let before: T | null = null;
        return this.db.ref(ref).transaction((existing: T | null) => {
            // Your transaction handler is called multiple times and must be able to handle null data. Even if there is existing data in your database it may not be locally cached when the transaction function is run.
            // @see https://firebase.google.com/docs/database/admin/save-data#section-transactions
            before = existing;
            if (!existing) return null;
            return checkAndUpdate(existing);
        }, undefined, false).then((tResult) => {
            result.committed = tResult.committed;
            result.after = tResult.snapshot.val();
            result.rollback = () => {
                if (before === null) {
                    return ref.remove();
                } else {
                    return ref.set(before);
                }
            };
            return result;
        });
    }
}

export {
    REFERENCE_USER_STATE_NAME,
    REFERENCE_USER_SESSION_NAME,
    REFERENCE_USER_HISTORY_NAME,
};
