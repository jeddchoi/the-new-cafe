import {getFirestore, Firestore, DocumentReference} from "firebase-admin/firestore";
import {Store, storeConverter} from "../model/Store";
import {Section, sectionConverter} from "../model/Section";
import {Seat, seatConverter, SeatStatusType} from "../model/Seat";
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

    static getStoreData(storeId: string): Promise<Store | undefined> {
        return this.getStore(storeId)
            .withConverter(storeConverter).get()
            .then((value) => value.data());
    }

    static getSection(storeId: string, sectionId: string): DocumentReference {
        return this.getStore(storeId)
            .collection(COLLECTION_GROUP_SECTION_NAME).doc(sectionId);
    }

    static getSectionData(storeId: string, sectionId: string): Promise<Section | undefined> {
        return this.getSection(storeId, sectionId)
            .withConverter(sectionConverter).get()
            .then((value) => value.data());
    }

    static getSeat(seatPosition: ISeatPosition): DocumentReference {
        return this.getSection(seatPosition.storeId, seatPosition.sectionId)
            .collection(COLLECTION_GROUP_SEAT_NAME).doc(seatPosition.seatId);
    }

    static getSeatData(seatPosition: ISeatPosition): Promise<Seat | undefined> {
        return this.getSeat(seatPosition)
            .withConverter(seatConverter).get()
            .then((value) => value.data());
    }

    static updateSeat(
        uid: string | undefined,
        seatPosition: ISeatPosition,
        targetStatus: SeatStatusType,
        isAvailable: boolean,
        predicate: ((exisingSeat: Seat | undefined) => boolean),
    ): Promise<void> {
        const ref = this.getSeat(seatPosition).withConverter(seatConverter);
        return ref.get().then((snapshot) => {
            const seat = snapshot.data();
            if (seat === undefined) return false;
            return predicate(seat) &&
                seat.seatId === seatPosition.seatId &&
                seat.sectionId === seatPosition.sectionId &&
                seat.storeId === seatPosition.storeId;
        }).then((canUpdate) => {
            if (canUpdate) {
                return ref.update(<Seat>{
                    currentUserId: uid,
                    status: targetStatus,
                    isAvailable,
                }).then();
            } else {
                return Promise.resolve();
            }
        });
    }
}

