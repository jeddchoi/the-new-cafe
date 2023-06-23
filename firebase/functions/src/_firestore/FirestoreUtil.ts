import {TransactionSupportUtil} from "../helper/TransactionSupportUtil";
import {TransactionResult} from "../helper/TransactionResult";
import {logger} from "firebase-functions/v2";
import {FirestoreDataConverter, getFirestore} from "firebase-admin/firestore";
import {firestore} from "firebase-admin";
import {ResultCode} from "../seat-finder/_enum/ResultCode";
import {isResultCode} from "../helper/isResultCode";

export class FirestoreUtil implements TransactionSupportUtil {
    private db: firestore.Firestore = getFirestore();

    transaction<T>(refPath: string, checkAndUpdate: (existing: (T | null)) => (T | null)): Promise<TransactionResult<T>> {
        logger.debug("[FirestoreUtil] transaction", {refPath});

        const ref = this.db.doc(refPath).withConverter(<FirestoreDataConverter<T>>{
            toFirestore(data: T) {
                return data;
            },
            fromFirestore(snapshot: FirebaseFirestore.QueryDocumentSnapshot): T {
                return snapshot.data() as T;
            },
        });
        return this.db.runTransaction(async (transaction) => {
            const existing = (await transaction.get(ref)).data() ?? null;
            const rollback = async () => {
                logger.debug("[FirestoreUtil] rollback called", {refPath});
                try {
                    if (!existing) {
                        logger.debug("[FirestoreUtil] ref will be removed", {refPath});
                        await ref.delete();
                    } else {
                        logger.debug("[FirestoreUtil] ref will be set as before", {refPath, before: existing});
                        await ref.set(existing);
                    }
                } catch (e) {
                    throw ResultCode.CORRUPTED;
                }
            };

            try {
                logger.debug("[FirestoreUtil] existing data", {existing});
                const newContent = checkAndUpdate(existing);
                logger.debug("[FirestoreUtil] new data", {newContent});
                if (newContent === null) {
                    transaction.delete(ref);
                } else {
                    transaction.set(ref, newContent);
                }

                return new TransactionResult<T>(existing, newContent, rollback, ResultCode.OK);
            } catch (e) { // abort
                if (isResultCode(e)) {
                    return new TransactionResult<T>(existing, null, rollback, e);
                } else {
                    return new TransactionResult<T>(existing, null, rollback);
                }
            }
        }).then((result) => {
            logger.debug("[FirestoreUtil] transaction result", {result});
            return result;
        });
    }
}

export const firestoreUtil = new FirestoreUtil();
