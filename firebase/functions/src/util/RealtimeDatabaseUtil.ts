import {Database, getDatabase} from "firebase-admin/database";
import {TransactionResult} from "../model/TransactionResult";
import {database} from "firebase-admin";
import {logger} from "firebase-functions/v2";

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
    static runTransactionOnRef<T>(ref: database.Reference, checkAndUpdate: (existing: T | null) => T | null) {
        const result = new TransactionResult<T>();
        return this.db.ref(ref).transaction((existing: T | null) => {
            // Your transaction handler is called multiple times and must be able to handle null data. Even if there is existing data in your database it may not be locally cached when the transaction function is run.
            // @see https://firebase.google.com/docs/database/admin/save-data#section-transactions
            logger.log(`Realtime existing == ${existing} / ${JSON.stringify(existing)}`);
            result.before = existing;
            const newContent = checkAndUpdate(existing);
            logger.log(`newContent = ${JSON.stringify(newContent)}`);
            return newContent;
        }, undefined).then((tResult) => {
            result.committed = tResult.committed;
            result.after = tResult.snapshot.val();
            result.rollback = () => {
                logger.warn("Rollback!");
                if (result.before === null) {
                    return ref.remove();
                } else {
                    return ref.set(result.before);
                }
            };

            logger.log("[Realtime] transaction result = " + JSON.stringify(result));
            return result;
        });
    }
}

export {
    REFERENCE_USER_STATE_NAME,
    REFERENCE_USER_SESSION_NAME,
    REFERENCE_USER_HISTORY_NAME,
};
