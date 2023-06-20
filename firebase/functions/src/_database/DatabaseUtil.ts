import {Database, getDatabase} from "firebase-admin/database";
import {TransactionSupportUtil} from "../helper/TransactionSupportUtil";
import {TransactionResult} from "../helper/TransactionResult";
import {logger} from "firebase-functions/v2";

export class DatabaseUtil implements TransactionSupportUtil {
    private db: Database = getDatabase();

    transaction<T>(refPath: string, checkAndUpdate: (existing: (T | null)) => (T | null)): Promise<TransactionResult<T>> {
        logger.debug("[DatabaseUtil] transaction", {refPath});
        const result = new TransactionResult<T>();
        const ref = this.db.ref(refPath);
        return ref.transaction((existing: T | null) => {
            result.before = existing;
            logger.debug("[DatabaseUtil] existing data", {existing});
            const newContent = checkAndUpdate(existing);
            logger.debug("[DatabaseUtil] new data", {newContent});
            return newContent;
        }).then((tResult) => {
            result.committed = tResult.committed;
            result.after = tResult.snapshot.val();
            result.rollback = () => {
                logger.debug("[DatabaseUtil] rollback called", {refPath});
                if (!result.before) {
                    logger.debug("[DatabaseUtil] ref will be removed", {refPath});
                    return ref.remove();
                } else {
                    logger.debug("[DatabaseUtil] ref will be set", {refPath});
                    return ref.set(result.before);
                }
            };
            logger.debug("[DatabaseUtil] transaction result", {result});
            return result;
        }).catch((err) => {
            logger.error("[DatabaseUtil] transaction error", {err});
            throw err;
        });
    }
}

export const databaseUtil = new DatabaseUtil();
