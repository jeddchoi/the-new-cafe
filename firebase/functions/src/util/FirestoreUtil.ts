import {DocumentReference, Firestore, getFirestore, UpdateData} from "firebase-admin/firestore";
import {IStoreExternal, Store, storeConverter} from "../model/Store";
import {ISeatExternal, Seat, seatConverter} from "../model/Seat";
import {ISectionExternal, Section, sectionConverter} from "../model/Section";
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

    static getStoreDocRef(storeId: string): DocumentReference<IStoreExternal> {
        return FirestoreUtil.db.collection(COLLECTION_GROUP_STORE_NAME).doc(storeId).withConverter(storeConverter);
    }

    static getSectionDocRef(storeId: string, sectionId: string): DocumentReference<ISectionExternal> {
        return this.getStoreDocRef(storeId)
            .collection(COLLECTION_GROUP_SECTION_NAME).doc(sectionId).withConverter(sectionConverter);
    }

    static getSeatDocRef(seatId: SeatPosition): DocumentReference<ISeatExternal> {
        return this.getSectionDocRef(seatId.storeId, seatId.sectionId)
            .collection(COLLECTION_GROUP_SEAT_NAME).doc(seatId.seatId).withConverter(seatConverter);
    }

    static runTransactionOnSingleRefDoc<T>(docRef: DocumentReference<T>, predicate: (data: T | undefined) => boolean, update: (existing: T | undefined) => UpdateData<T>): Promise<boolean> {
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

