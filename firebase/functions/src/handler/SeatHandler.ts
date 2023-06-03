import {ISeatPosition} from "../model/UserState";
import {Seat} from "../model/Seat";
import FirestoreUtil from "../util/FirestoreUtil";


export default class SeatHandler {
    static getSeatData(seatPosition: ISeatPosition): Promise<Seat | undefined> {
        return FirestoreUtil.getSeatDocRef(seatPosition).get()
            .then((value) => value.data());
    }

    static updateSeat(
        seatPosition: ISeatPosition,
        updateContent: { [key in keyof Seat]?: Seat[key] }
    ): Promise<void> {
        return FirestoreUtil.getSeatDocRef(seatPosition).update(updateContent).then();
    }

    static transaction(
        seatPosition: ISeatPosition,
        predicate: (existing: Seat|undefined) => boolean,
        update: (existing: Seat | undefined) => { [key in keyof Seat]?: Seat[key] }
    ): Promise<boolean> {
        return FirestoreUtil.runTransactionOnSingleRefDoc(FirestoreUtil.getSeatDocRef(seatPosition), predicate, update);
    }
}
