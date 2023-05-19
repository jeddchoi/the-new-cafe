import * as admin from "firebase-admin";
import {Store, storeConverter} from "../model/Store";
import {Section, sectionConverter} from "../model/Section";
import {Seat, seatConverter, SeatStatusType} from "../model/Seat";


export const COLLECTION_GROUP_STORE_NAME = "stores";
export const COLLECTION_GROUP_SECTION_NAME = "sections";
export const COLLECTION_GROUP_SEAT_NAME = "seats";

/**
 * Utility class for Firestore
 */
export default class FirestoreUtil {
    static db: admin.firestore.Firestore = admin.firestore();

    static getStore(storeId: string): admin.firestore.DocumentReference {
        return this.db
            .collection(COLLECTION_GROUP_STORE_NAME).doc(storeId);
    }

    static getStoreData(storeId: string): Promise<Store | undefined> {
        return this.getStore(storeId)
            .withConverter(storeConverter).get()
            .then((value) => value.data());
    }

    static getSection(storeId: string, sectionId: string): admin.firestore.DocumentReference {
        return this.getStore(storeId)
            .collection(COLLECTION_GROUP_SECTION_NAME).doc(sectionId);
    }

    static getSectionData(storeId: string, sectionId: string): Promise<Section | undefined> {
        return this.getSection(storeId, sectionId)
            .withConverter(sectionConverter).get()
            .then((value) => value.data());
    }

    static getSeat(storeId: string, sectionId: string, seatId: string): admin.firestore.DocumentReference {
        return this.getSection(storeId, sectionId)
            .collection(COLLECTION_GROUP_SEAT_NAME).doc(seatId);
    }

    static getSeatData(storeId: string, sectionId: string, seatId: string): Promise<Seat | undefined> {
        return this.getSeat(storeId, sectionId, seatId)
            .withConverter(seatConverter).get()
            .then((value) => value.data());
    }

    static updateSeat(
        storeId: string,
        sectionId: string,
        seatId: string,
        status: SeatStatusType,
        isAvailable: boolean,
        uid: string | undefined = undefined,
    ): Promise<void> {
        return this.getSeat(storeId, sectionId, seatId)
            .withConverter(seatConverter).update({
                currentUserId: uid,
                status: status,
                isAvailable: isAvailable,
            }).then();
    }
}
