import {DocumentData, FirestoreDataConverter, QueryDocumentSnapshot} from "firebase-admin/firestore";
import {throwFunctionsHttpsError} from "../util/functions_helper";
import {SeatId} from "./SeatId";

enum SeatStateType {
    Empty,
    Reserved,
    Occupied,
    Away,
    Restricted,
}

interface ISeat {
    seatId: SeatId,
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
        readonly seatId: SeatId,
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
            {
                storeId: snapshot.ref.parent.parent?.id ?? throwFunctionsHttpsError("internal", "store doesn't exist"),
                sectionId: snapshot.ref.parent.id,
                seatId: snapshot.id,
            },
            data.name,
            data.state,
            data.isAvailable,
            data.userId ?? null);
    },
};

export {Seat, ISeat, ISeatExternal, SeatStateType, seatConverter};
