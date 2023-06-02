import {getFirestore, Firestore, DocumentReference} from "firebase-admin/firestore";
import {ISeatPosition} from "../model/UserStatus";


export const COLLECTION_GROUP_STORE_NAME = "stores";
export const COLLECTION_GROUP_SECTION_NAME = "sections";
export const COLLECTION_GROUP_SEAT_NAME = "seats";

/**
 * Utility class for Firestore
 */
export default class FirestoreUtil {
    static db: Firestore = getFirestore();

    static getStore(storeId: string): DocumentReference {
        return this.db
            .collection(COLLECTION_GROUP_STORE_NAME).doc(storeId);
    }

    static getSection(storeId: string, sectionId: string): DocumentReference {
        return this.getStore(storeId)
            .collection(COLLECTION_GROUP_SECTION_NAME).doc(sectionId);
    }

    static getSeat(seatPosition: ISeatPosition): DocumentReference {
        return this.getSection(seatPosition.storeId, seatPosition.sectionId)
            .collection(COLLECTION_GROUP_SEAT_NAME).doc(seatPosition.seatId);
    }
}

