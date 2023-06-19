import {DocumentReference, Firestore, getFirestore} from "firebase-admin/firestore";
import {storeConverter} from "../model/Store";
import {seatConverter} from "../model/Seat";
import {sectionConverter} from "../model/Section";
import {
    COLLECTION_GROUP_SEAT_NAME,
    COLLECTION_GROUP_SECTION_NAME,
    COLLECTION_GROUP_STORE_NAME,
    SeatPosition,
} from "../model/SeatPosition";
import {TransactionResult} from "../model/TransactionResult";
import {logger} from "firebase-functions/v2";
import {firestore} from "firebase-admin";
import UpdateData = firestore.UpdateData;


/**
 * Utility class for Firestore
 */
export default class FirestoreUtil {
    static db: Firestore = getFirestore();

    static getStoreDocRef(storeId: string) {
        return FirestoreUtil.db.collection(COLLECTION_GROUP_STORE_NAME).withConverter(storeConverter).doc(storeId);
    }

    static getSectionDocRef(storeId: string, sectionId: string) {
        return this.getStoreDocRef(storeId)
            .collection(COLLECTION_GROUP_SECTION_NAME).withConverter(sectionConverter).doc(sectionId);
    }

    static getSeatDocRef(seatId: SeatPosition | string) {
        if (typeof seatId === "string") {
            return FirestoreUtil.db.doc(seatId).withConverter(seatConverter);
        } else {
            return this.getSectionDocRef(seatId.storeId, seatId.sectionId)
                .collection(COLLECTION_GROUP_SEAT_NAME).withConverter(seatConverter).doc(seatId.seatId);
        }
    }

    // in Firestore world, absence of data(including field) is undefined, not null (null means explicit null)
    static runTransactionOnSingleRefDoc<T>(docRef: DocumentReference<T>, checkAndUpdate: (existing: T) => UpdateData<T>) {
        return this.db.runTransaction(async (t) => {
            const result = new TransactionResult<T>();
            const data = (await t.get(docRef)).data();
            result.before = data;
            if (!data) {
                logger.debug(`data = ${data}`);
                return result;
            }
            const newContent = checkAndUpdate(data);
            logger.log(`newContent = ${JSON.stringify(newContent)}`);
            t.update(docRef, newContent);
            result.committed = true;
            result.after = {
                ...data,
                newContent,
            };
            return result;
        }).then((result) => {
            result.rollback = () => {
                logger.warn("Rollback!");
                if (!result.before) {
                    return docRef.delete();
                } else {
                    return docRef.set(result.before);
                }
            };
            logger.log("[Firestore] transaction result = " + JSON.stringify(result));
            return result;
        });
    }
}

