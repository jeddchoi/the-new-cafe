import {Database, getDatabase} from "firebase-admin/database";
import {TransactionSupportUtil} from "../helper/TransactionSupportUtil";
import {TransactionResult} from "../helper/TransactionResult";
import {logger} from "firebase-functions/v2";
import {database} from "firebase-admin";
import {ResultCode} from "../seat-finder/_enum/ResultCode";

const REFERENCE_SEAT_FINDER_NAME = "seatFinder";


export class DatabaseUtil implements TransactionSupportUtil {
    private db: Database = getDatabase();

    getRefPath(ref: database.Reference) {
        return ref.toString().substring(ref.root.toString().length - 1);
    }

    seatFinderRef(): database.Reference {
        return this.db.ref().child(REFERENCE_SEAT_FINDER_NAME);
    }

    transaction<T>(refPath: string, checkAndUpdate: (existing: (T | null)) => (T | null)): Promise<TransactionResult<T>> {
        logger.debug("[DatabaseUtil] transaction", {refPath});
        const result = new TransactionResult<T>();
        const ref = this.db.ref(refPath);
        let capturedError: unknown;

        return ref.transaction((existing: T | null) => {
            result.before = existing;
            logger.debug("[DatabaseUtil] existing data", {existing});
            try {
                const newContent = checkAndUpdate(existing);
                logger.debug("[DatabaseUtil] new data", {newContent});
                return newContent;
            } catch (e) {
                logger.error(`captured error : ${e}`);
                capturedError = e;
                return;
            }
        }).then((tResult) => {
            if (!tResult.committed) {
                throw capturedError ?? ResultCode.REJECTED;
            }
            result.after = tResult.snapshot.val();
            result.rollback = () => {
                logger.debug("[DatabaseUtil] rollback called", {refPath});
                if (!result.before) {
                    logger.debug("[DatabaseUtil] ref will be removed", {refPath});
                    return ref.remove();
                } else {
                    logger.debug("[DatabaseUtil] ref will be set as before", {refPath});
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
