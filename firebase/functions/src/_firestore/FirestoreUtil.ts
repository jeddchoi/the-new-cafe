import {TransactionSupportUtil} from "../helper/TransactionSupportUtil";
import {TransactionResult} from "../helper/TransactionResult";
import {logger} from "firebase-functions/v2";
import {FirestoreDataConverter, getFirestore} from "firebase-admin/firestore";
import {firestore} from "firebase-admin";

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
            const result = new TransactionResult<T>();
            const existing = (await transaction.get(ref)).data() ?? null;
            result.before = existing;
            logger.debug("[FirestoreUtil] existing data", {existing});
            const newContent = checkAndUpdate(existing);
            logger.debug("[FirestoreUtil] new data", {newContent});
            if (newContent === null) {
                transaction.delete(ref);
            } else {
                transaction.set(ref, newContent);
            }
            result.committed = true;
            result.after = newContent;
            result.rollback = () => {
                logger.debug("[FirestoreUtil] rollback called", {refPath});
                if (!result.before) {
                    logger.debug("[FirestoreUtil] ref will be removed", {refPath});
                    return ref.delete();
                } else {
                    logger.debug("[FirestoreUtil] ref will be set", {refPath});
                    return ref.set(result.before);
                }
            };
            logger.debug("[FirestoreUtil] transaction result", {result});
            return result;
        }).catch((err) => {
            logger.error("[FirestoreUtil] transaction error", {err});
            throw err;
        });
    }
}

export const firestoreUtil = new FirestoreUtil();
