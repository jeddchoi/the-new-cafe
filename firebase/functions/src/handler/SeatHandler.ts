import {Seat} from "../model/Seat";
import FirestoreUtil from "../util/FirestoreUtil";
import {SeatId} from "../model/SeatId";


export default class SeatHandler {
    static getSeatData(seatPosition: SeatId): Promise<Seat | undefined> {
        return FirestoreUtil.getSeatDocRef(seatPosition).get()
            .then((value) => value.data());
    }

    static updateSeat(
        seatPosition: SeatId,
        updateContent: { [key in keyof Seat]?: Seat[key] }
    ): Promise<void> {
        return FirestoreUtil.getSeatDocRef(seatPosition).update(updateContent).then();
    }

    static transaction(
        seatPosition: SeatId,
        predicate: (existing: Seat|undefined) => boolean,
        update: (existing: Seat | undefined) => { [key in keyof Seat]?: Seat[key] }
    ): Promise<boolean> {
        return FirestoreUtil.runTransactionOnSingleRefDoc(FirestoreUtil.getSeatDocRef(seatPosition), predicate, update);
    }
}
