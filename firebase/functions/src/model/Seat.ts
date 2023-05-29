import {QueryDocumentSnapshot, FirestoreDataConverter, DocumentData} from "firebase-admin/firestore";
import {throwFunctionsHttpsError} from "../util/functions_helper";

enum SeatStatusType {
    None,
    Reserved,
    Occupied,
    Away,
    Restricted,
}

interface ISeat {
    uid: string,
    storeId: string,
    sectionId: string,
    name: string,
    status: SeatStatusType,
    isAvailable: boolean,
    currentUserId?: string,
}

interface ISeatExternal {
    name: string,
    status: number,
    isAvailable: boolean,
    currentUserId?: string,
}

class Seat implements ISeat {
    constructor(
        readonly uid: string,
        readonly storeId: string,
        readonly sectionId: string,
        readonly name: string,
        readonly status: SeatStatusType,
        readonly isAvailable: boolean,
        readonly currentUserId?: string,
    ) {
    }
}


const seatConverter: FirestoreDataConverter<Seat> = {
    toFirestore(seat: Seat): DocumentData {
        return {
            name: seat.name,
            status: seat.status,
            isAvailable: seat.isAvailable,
            currentUserId: seat.currentUserId,
        };
    },
    fromFirestore(
        snapshot: QueryDocumentSnapshot<ISeatExternal>
    ): Seat {
        const data = snapshot.data();
        return new Seat(snapshot.id,
            snapshot.ref.parent.parent?.id ?? throwFunctionsHttpsError("internal", "Section doesn't exist"),
            snapshot.ref.parent.id,
            data.name,
            data.status,
            data.isAvailable,
            data.currentUserId);
    },
};

export {Seat, ISeat, ISeatExternal, SeatStatusType, seatConverter};
