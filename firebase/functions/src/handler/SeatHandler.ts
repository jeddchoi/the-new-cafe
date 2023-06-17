import {Seat, SeatStateType} from "../model/Seat";
import FirestoreUtil from "../util/FirestoreUtil";
import {SeatPosition} from "../model/SeatPosition";
import {https, logger} from "firebase-functions/v2";
import {BusinessResultCode} from "../model/BusinessResultCode";


export default class SeatHandler {
    static reserveSeat(userId: string, seatPosition: SeatPosition, endTime: number | null) {
        logger.debug(`[SeatHandler] reserveSeat(${userId}, ${JSON.stringify(seatPosition)}, ${endTime})`);
        return this.transaction(seatPosition, (existing) => {
            if (existing.userId || existing.state !== SeatStateType.Empty || !existing.isAvailable) {
                throw BusinessResultCode.SEAT_NOT_AVAILABLE;
            }
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
            if (existing.isAvailable || existing.state !== SeatStateType.Reserved || existing.userId !== userId) {
                throw new https.HttpsError("data-loss", "System state is corrupted");
            }
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
            if (existing.isAvailable || existing.userId !== userId) {
                throw new https.HttpsError("data-loss", "System state is corrupted");
            }

            return {
                state: SeatStateType.Empty,
                isAvailable: true,
                userId: null,
                reserveEndTime: null,
                occupyEndTime: null,
            };
        });
    }

    static resumeUsing(userId: string, seatPosition: SeatPosition | string) {
        logger.debug(`[SeatHandler] resumeUsing(${userId}, ${JSON.stringify(seatPosition)})`);
        return this.transaction(seatPosition, (existing) => {
            if (existing.isAvailable || existing.userId !== userId) {
                throw new https.HttpsError("data-loss", "System state is corrupted");
            }
            return {
                state: SeatStateType.Occupied,
            };
        });
    }

    static away(userId: string, seatPosition: SeatPosition | string) {
        logger.debug(`[SeatHandler] away(${userId}, ${JSON.stringify(seatPosition)})`);
        return this.transaction(seatPosition, (existing) => {
            if (existing.isAvailable || existing.userId !== userId) {
                throw new https.HttpsError("data-loss", "System state is corrupted");
            }
            return {
                state: SeatStateType.Away,
            };
        });
    }

    static updateReserveEndTime(userId: string, seatPosition: SeatPosition | string, newEndTime: number | null) {
        logger.debug(`[SeatHandler] updateReserveEndTime(${userId}, ${JSON.stringify(seatPosition)}, ${newEndTime})`);
        return this.transaction(seatPosition, (existing) => {
            if (existing.isAvailable || existing.state !== SeatStateType.Reserved || existing.userId !== userId) {
                throw new https.HttpsError("data-loss", "System state is corrupted");
            }

            return {
                reserveEndTime: newEndTime,
            };
        });
    }

    static updateOccupyEndTime(userId: string, seatPosition: SeatPosition | string, newEndTime: number | null) {
        logger.debug(`[SeatHandler] updateOccupyEndTime(${userId}, ${JSON.stringify(seatPosition)}, ${newEndTime})`);
        return this.transaction(seatPosition, (existing) => {
            if (existing.isAvailable || existing.state !== SeatStateType.Occupied || existing.userId !== userId) {
                throw new https.HttpsError("data-loss", "System state is corrupted");
            }
            return {
                occupyEndTime: newEndTime,
            };
        });
    }

    static setSeat(seatPosition: SeatPosition, seat: Seat) {
        return FirestoreUtil.getSeatDocRef(seatPosition).set(seat);
    }

    static getSeatData(seatPosition: SeatPosition | string) {
        logger.debug(`[SeatHandler] getSeatData(${seatPosition})`);
        return FirestoreUtil.getSeatDocRef(seatPosition).get()
            .then((value) => value.data());
    }

    static transaction(
        seatPosition: SeatPosition | string,
        update: (existing: Seat) => Partial<Seat>
    ) {
        return FirestoreUtil.runTransactionOnSingleRefDoc(FirestoreUtil.getSeatDocRef(seatPosition), update);
    }

    updateSeat(
        seatPosition: SeatPosition | string,
        updateContent: Partial<Seat>
    ) {
        return FirestoreUtil.getSeatDocRef(seatPosition).update(updateContent).then();
    }
}
