import {getFirestore, Firestore, DocumentReference, UpdateData} from "firebase-admin/firestore";
import {ISeatPosition} from "../model/UserState";
import {Store, storeConverter} from "../model/Store";
import {Seat, seatConverter} from "../model/Seat";
import {Section, sectionConverter} from "../model/Section";


export const COLLECTION_GROUP_STORE_NAME = "stores";
export const COLLECTION_GROUP_SECTION_NAME = "sections";
export const COLLECTION_GROUP_SEAT_NAME = "seats";

/**
 * Utility class for Firestore
 */
export default class FirestoreUtil {
    static db: Firestore = getFirestore();

    static getStoreDocRef(storeId: string): DocumentReference<Store> {
        return FirestoreUtil.db.collection(COLLECTION_GROUP_STORE_NAME).doc(storeId).withConverter(storeConverter);
    }

    static getSectionDocRef(storeId: string, sectionId: string): DocumentReference<Section> {
        return this.getStoreDocRef(storeId)
            .collection(COLLECTION_GROUP_SECTION_NAME).doc(sectionId).withConverter(sectionConverter);
    }

    static getSeatDocRef(seatPosition: ISeatPosition): DocumentReference<Seat> {
        return this.getSectionDocRef(seatPosition.storeId, seatPosition.sectionId)
            .collection(COLLECTION_GROUP_SEAT_NAME).doc(seatPosition.seatId).withConverter(seatConverter);
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

