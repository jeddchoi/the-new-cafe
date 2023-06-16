import {FirestoreDataConverter, QueryDocumentSnapshot} from "firebase-admin/firestore";
import {SeatPosition} from "./SeatPosition";
import {https} from "firebase-functions/v2";

enum SeatStateType {
    Empty,
    Reserved,
    Occupied,
    Away,
    Restricted,
}

interface ISeat {
    seatPosition: SeatPosition,
    name: string,
    state: SeatStateType,
    isAvailable: boolean,
    userId: string | null,
    reserveEndTime: number | null,
    occupyEndTime: number | null,
}

interface ISeatExternal {
    name: string,
    state: SeatStateType,
    isAvailable: boolean,
    userId: string | null,
    reserveEndTime: number | null,
    occupyEndTime: number | null,
}

class Seat implements ISeat {
    constructor(
        readonly seatPosition: SeatPosition,
        readonly name: string,
        readonly state: SeatStateType,
        readonly isAvailable: boolean,
        readonly userId: string | null,
        readonly reserveEndTime: number | null,
        readonly occupyEndTime: number | null,
    ) {
    }
}


const seatConverter: FirestoreDataConverter<Seat> = {
    toFirestore(seat: Seat) {
        return <ISeatExternal>{
            name: seat.name,
            state: seat.state,
            isAvailable: seat.isAvailable,
            userId: seat.userId,
        };
    },
    fromFirestore(
        snapshot: QueryDocumentSnapshot<ISeatExternal>
    ) {
        const data = snapshot.data();

        const storeId = snapshot.ref.parent.parent?.parent.parent?.id;
        const sectionId = snapshot.ref.parent.parent?.id;

        if (!storeId || !sectionId) {
            throw new https.HttpsError("not-found", "store doesn't exist");
        }

        return new Seat(
            {
                storeId,
                sectionId,
                seatId: snapshot.id,
            },
            data.name,
            data.state,
            data.isAvailable,
            data.userId ?? null,
            data.reserveEndTime,
            data.occupyEndTime,
        );
    },
};

export {Seat, ISeat, ISeatExternal, SeatStateType, seatConverter};
