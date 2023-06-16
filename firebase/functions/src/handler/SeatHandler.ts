import {ISeatExternal, Seat, SeatStateType} from "../model/Seat";
import FirestoreUtil from "../util/FirestoreUtil";
import {SeatPosition} from "../model/SeatPosition";
import {logger} from "firebase-functions/v2";


export default class SeatHandler {
    static getSeatData(seatPosition: SeatPosition | string) {
        logger.debug(`[SeatHandler] getSeatData(${seatPosition})`);
        return FirestoreUtil.getSeatDocRef(seatPosition).get()
            .then((value) => value.data());
    }

    static reserveSeat(userId: string, seatPosition: SeatPosition, endTime: number | null) {
        logger.debug(`[SeatHandler] reserveSeat(${userId}, ${JSON.stringify(seatPosition)}, ${endTime})`);
        return this.transaction(seatPosition, (existing) => {
            if (!existing) return false;
            if (existing.userId) return false;
            if (existing.state !== SeatStateType.Empty) return false;

            return existing.isAvailable;
        }, () => {
            return {
                state: SeatStateType.Reserved,
                isAvailable: false,
                userId,
                reserveEndTime: endTime,
            };
        });
    }

    static occupySeat(userId: string, seatPosition: SeatPosition | string, occupyEndTime: number | null) {
        logger.debug(`[SeatHandler] occupySeat(${userId}, ${JSON.stringify(seatPosition)}, ${occupyEndTime})`);
        return this.transaction(seatPosition, (existing) => {
            if (!existing) return false;
            if (existing.isAvailable) return false;
            if (existing.state !== SeatStateType.Reserved) return false;
            return existing.userId === userId;
        }, () => {
            return {
                state: SeatStateType.Occupied,
                reserveEndTime: null,
                occupyEndTime,
            };
        });
    }

    static freeSeat(userId: string, seatPosition: SeatPosition | string) {
        logger.debug(`[SeatHandler] freeSeat(${userId}, ${JSON.stringify(seatPosition)})`);
        return this.transaction(seatPosition, (existing) => {
            if (!existing) return false;
            if (existing.isAvailable) return false;
            return existing.userId === userId;
        }, () => {
            return {
                state: SeatStateType.Empty,
                isAvailable: true,
                userId: null,
                reserveEndTime: null,
                occupyEndTime: null,
            };
        }).then();
    }

    static resumeUsing(userId: string, seatPosition: SeatPosition | string) {
        logger.debug(`[SeatHandler] resumeUsing(${userId}, ${JSON.stringify(seatPosition)})`);
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

    static away(userId: string, seatPosition: SeatPosition | string) {
        logger.debug(`[SeatHandler] away(${userId}, ${JSON.stringify(seatPosition)})`);
        return this.transaction(seatPosition, (existing) => {
            if (!existing) return false;
            if (existing.isAvailable) return false;
            return existing.userId === userId;
        }, () => {
            return {
                state: SeatStateType.Away,
            };
        });
    }

    static updateReserveEndTime(userId: string, seatPosition: SeatPosition | string, newEndTime: number | null) {
        logger.debug(`[SeatHandler] updateReserveEndTime(${userId}, ${JSON.stringify(seatPosition)}, ${newEndTime})`);
        return this.transaction(seatPosition, (existing) => {
            if (!existing) return false;
            if (existing.isAvailable) return false;
            if (existing.state !== SeatStateType.Reserved) return false;
            return existing.userId === userId;
        }, () => {
            return {
                reserveEndTime: newEndTime,
            };
        });
    }

    static updateOccupyEndTime(userId: string, seatPosition: SeatPosition | string, newEndTime: number | null) {
        logger.debug(`[SeatHandler] updateOccupyEndTime(${userId}, ${JSON.stringify(seatPosition)}, ${newEndTime})`);
        return this.transaction(seatPosition, (existing) => {
            if (!existing) return false;
            if (existing.isAvailable) return false;
            if (existing.state !== SeatStateType.Occupied) return false;
            return existing.userId === userId;
        }, () => {
            return {
                occupyEndTime: newEndTime,
            };
        });
    }

    static transaction(
        seatPosition: SeatPosition | string,
        predicate: (existing: Seat | undefined) => boolean,
        update: (existing: Seat | undefined) => Partial<Seat>
    ) {
        return FirestoreUtil.runTransactionOnSingleRefDoc(FirestoreUtil.getSeatDocRef(seatPosition), predicate, update);
    }

    updateSeat(
        seatPosition: SeatPosition | string,
        updateContent: { [key in keyof ISeatExternal]?: ISeatExternal[key] }
    ) {
        return FirestoreUtil.getSeatDocRef(seatPosition).update(updateContent).then();
    }
}
