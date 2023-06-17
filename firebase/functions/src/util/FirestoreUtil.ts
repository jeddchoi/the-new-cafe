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
    static runTransactionOnSingleRefDoc<T>(docRef: DocumentReference<T>, checkAndUpdate: (existing: T) => Partial<T>) {
        let before: T | undefined = undefined;
        return this.db.runTransaction(async (t) => {
            const result = new TransactionResult<T>();
            const data = (await t.get(docRef)).data();
            before = data;
            if (!data) return result;
            const newContent = checkAndUpdate(data);
            t.set(docRef, newContent, {merge: true});
            result.committed = true;
            result.after = <T>{
                ...data,
                ...newContent,
            };
            return result;
        }).then((result) => {
            result.rollback = () => {
                if (before === undefined) {
                    return docRef.delete();
                } else {
                    return docRef.set(before);
                }
            };
            return result;
        });
    }
}

