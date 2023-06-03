import {ISeatPosition} from "../model/UserState";
import {ISeatExternal, Seat, seatConverter} from "../model/Seat";
import FirestoreUtil from "../util/FirestoreUtil";


export default class SeatHandler {
    static getSeatData(seatPosition: ISeatPosition): Promise<Seat | undefined> {
        return FirestoreUtil.getSeat(seatPosition)
            .withConverter(seatConverter).get()
            .then((value) => value.data());
    }

    static updateSeat(
        seatPosition: ISeatPosition,
        updateContent: { [key in keyof ISeatExternal]?: ISeatExternal[key] }
    ): Promise<void> {
        return FirestoreUtil.getSeat(seatPosition)
            .update(updateContent).then();
    }
}
