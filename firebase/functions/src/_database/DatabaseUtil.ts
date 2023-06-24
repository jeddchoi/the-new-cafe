import {Database, getDatabase} from "firebase-admin/database";
import {TransactionSupportUtil} from "../helper/TransactionSupportUtil";
import {TransactionResult} from "../helper/TransactionResult";
import {logger} from "firebase-functions/v2";
import {database} from "firebase-admin";
import {ResultCode} from "../seat-finder/_enum/ResultCode";
import {isResultCode} from "../helper/isResultCode";

const REFERENCE_SEAT_FINDER_NAME = "seatFinder";


export class DatabaseUtil implements TransactionSupportUtil {
    private static db: Database = getDatabase();

    getRefPath(ref: database.Reference) {
        return ref.toString().substring(ref.root.toString().length - 1);
    }

    seatFinderRef(): database.Reference {
        return DatabaseUtil.db.ref().child(REFERENCE_SEAT_FINDER_NAME);
    }

    transaction<T>(refPath: string, checkAndUpdate: (existing: (T | null)) => (T | null)): Promise<TransactionResult<T>> {
        logger.debug("[DatabaseUtil] transaction", {refPath});
        const ref = DatabaseUtil.db.ref(refPath);
        let before: T | null;
        let resultCode: ResultCode;

        return ref.transaction((existing: T | null) => {
            before = existing;
            logger.debug("[DatabaseUtil] existing data", {before});
            try {
                const newContent = checkAndUpdate(before);
                logger.debug("[DatabaseUtil] new data", {newContent});
                return newContent;
            } catch (e) { // abort
                logger.error(`captured error : ${e}`);
                if (isResultCode(e)) {
                    resultCode = e;
                }
                return;
            }
        }).then((tResult) => {
            if (!tResult.committed) {
                logger.error("[DatabaseUtil] NOT committed", {resultCode});
                resultCode = resultCode ?? ResultCode.REJECTED;
            }
            return new TransactionResult<T>(
                before,
                tResult.snapshot.val(),
                () => {
                    logger.debug("[DatabaseUtil] rollback called", {refPath});
                    try {
                        if (!before) {
                            logger.debug("[DatabaseUtil] ref will be removed", {refPath});
                            return ref.remove();
                        } else {
                            logger.debug("[DatabaseUtil] ref will be set as before", {refPath, before});
                            return ref.set(before);
                        }
                    } catch (e) {
                        throw ResultCode.CORRUPTED;
                    }
                },
                resultCode ?? ResultCode.OK
            );
        }).then((result) => {
            logger.debug("[DatabaseUtil] transaction result", {result});
            return result;
        });
    }
}
