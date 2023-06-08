import {DocumentData, FirestoreDataConverter, QueryDocumentSnapshot} from "firebase-admin/firestore";
import {throwFunctionsHttpsError} from "../util/functions_helper";

enum SeatStateType {
    Empty,
    Reserved,
    Occupied,
    Away,
    Restricted,
}

interface ISeat {
    seatId: string,
    storeId: string,
    sectionId: string,
    name: string,
    state: SeatStateType,
    isAvailable: boolean,
    userId: string | null,
}

interface ISeatExternal {
    name: string,
    state: number,
    isAvailable: boolean,
    userId: string | null,
}

class Seat implements ISeat {
    constructor(
        readonly storeId: string,
        readonly sectionId: string,
        readonly seatId: string,
        readonly name: string,
        readonly state: SeatStateType,
        readonly isAvailable: boolean,
        readonly userId: string | null,
    ) {
    }
}


const seatConverter: FirestoreDataConverter<Seat> = {
    toFirestore(seat: Seat): DocumentData {
        return <ISeatExternal>{
            name: seat.name,
            state: seat.state,
            isAvailable: seat.isAvailable,
            userId: seat.userId,
        };
    },
    fromFirestore(
        snapshot: QueryDocumentSnapshot<ISeatExternal>
    ): Seat {
        const data = snapshot.data();
        return new Seat(
            snapshot.ref.parent.parent?.id ?? throwFunctionsHttpsError("internal", "store doesn't exist"),
            snapshot.ref.parent.id,
            snapshot.id,
            data.name,
            data.state,
            data.isAvailable,
            data.userId ?? null);
    },
};

export {Seat, ISeat, ISeatExternal, SeatStateType, seatConverter};
