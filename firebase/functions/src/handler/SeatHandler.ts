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
        logger.debug(`[SeatHandler] reserveSeat(${userId}, ${seatPosition}, ${endTime})`);
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

    static occupySeat(userId: string, seatPosition: SeatPosition, occupyEndTime: number | null) {
        logger.debug(`[SeatHandler] occupySeat(${userId}, ${seatPosition}, ${occupyEndTime})`);
        return this.transaction(seatPosition, (existing) => {
            if (!existing) return false;
            if (existing.isAvailable) return false;
            if (existing.reserveEndTime || existing.state !== SeatStateType.Empty) return false;
            return existing.userId === userId;
        }, (existing) => {
            return {
                state: SeatStateType.Occupied,
                reserveEndTime: null,
                occupyEndTime,
            };
        });
    }

    static freeSeat(userId: string, seatPosition: SeatPosition) {
        logger.debug(`[SeatHandler] freeSeat(${userId}, ${seatPosition})`);
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


    static resumeUsing(userId: string, seatPosition : SeatPosition) {
        logger.debug(`[SeatHandler] resumeUsing(${userId}, ${seatPosition})`);
        return this.transaction(seatPosition, (existing) => {
            if (!existing) return false;
            if (existing.isAvailable) return false;
            return existing.userId === userId;
        }, () => {
            return {
                state: SeatStateType.Occupied,
            };
        });
    }

    static away(userId: string, seatPosition: SeatPosition) {
        logger.debug(`[SeatHandler] away(${userId}, ${seatPosition})`);
        return this.transaction(seatPosition, (existing) => {
            if (!existing) return false;
            if (existing.isAvailable) return false;
            return existing.userId === userId;
        }, (existing) => {
            return {
                state: SeatStateType.Away,
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
