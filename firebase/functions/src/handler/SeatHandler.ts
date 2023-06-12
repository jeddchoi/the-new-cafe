import {ISeatExternal, Seat, SeatStateType} from "../model/Seat";
import FirestoreUtil from "../util/FirestoreUtil";
import {SeatPosition} from "../model/SeatPosition";
import {logger} from "firebase-functions/v2";


export default class SeatHandler {
    static getSeatData(seatPosition: SeatPosition) {
        return FirestoreUtil.getSeatDocRef(seatPosition).get()
            .then((value) => value.data());
    }

    static reserveSeat(userId: string, seatPosition: SeatPosition, endTime: number | null) {
        logger.debug(`reserveSeat(${userId}, ${seatPosition}, ${endTime})`);
        return this.transaction(seatPosition, (existing) => {
            if (!existing) return false;
            if (existing.userId) return false;
            if (existing.occupyEndTime) return false;
            if (existing.reserveEndTime) return false;
            if (existing.state !== SeatStateType.Empty) return false;

            return existing.isAvailable;
        }, (existing) => {
            return {
                state: SeatStateType.Reserved,
                isAvailable: false,
                userId,
                reserveEndTime: endTime,
            };
        });
    }

    static freeSeat(userId: string, seatPosition: SeatPosition) {
        return this.transaction(seatPosition, (existing) => {
            if (!existing) return false;
            if (existing.isAvailable) return false;
            return existing.userId === userId;
        }, (existing) => {
            return {
                state: SeatStateType.Empty,
                isAvailable: true,
                userId: null,
                reserveEndTime: null,
                occupyEndTime: null,
            };
        }).then();
    }


    static updateSeatInSession(userId: string, seatPosition: SeatPosition, seatState: SeatStateType, reserveEndTime?: number | null, occupyEndTime?: number | null) {
        return this.transaction(seatPosition, (existing) => {
            if (!existing) return false;
            if (existing.isAvailable) return false;
            return existing.userId === userId;
        }, (existing) => {
            return {
                state: seatState,
                reserveEndTime,
                occupyEndTime,
            };
        });
    }

    static transaction(
        seatPosition: SeatPosition,
        predicate: (existing: Seat | undefined) => boolean,
        update: (existing: Seat | undefined) => { [key in keyof ISeatExternal]?: ISeatExternal[key] }
    ) {
        return FirestoreUtil.runTransactionOnSingleRefDoc(FirestoreUtil.getSeatDocRef(seatPosition), predicate, update);
    }

    updateSeat(
        seatPosition: SeatPosition,
        updateContent: { [key in keyof ISeatExternal]?: ISeatExternal[key] }
    ) {
        return FirestoreUtil.getSeatDocRef(seatPosition).update(updateContent).then();
    }
}
