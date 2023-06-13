import {DocumentReference, Firestore, getFirestore, UpdateData} from "firebase-admin/firestore";
import {storeConverter} from "../model/Store";
import {seatConverter} from "../model/Seat";
import {sectionConverter} from "../model/Section";
import {
    COLLECTION_GROUP_SEAT_NAME,
    COLLECTION_GROUP_SECTION_NAME,
    COLLECTION_GROUP_STORE_NAME,
    SeatPosition,
} from "../model/SeatPosition";


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

    static runTransactionOnSingleRefDoc<T>(docRef: DocumentReference<T>, predicate: (data: T | undefined) => boolean, update: (existing: T | undefined) => UpdateData<T>) {
        return this.db.runTransaction(async (t) => {
            const data = (await t.get(docRef)).data();
            if (!predicate(data)) {
                return false;
            }
            const newContent = update(data);
            t.update(docRef, newContent);
            return true;
        });
    }
}

