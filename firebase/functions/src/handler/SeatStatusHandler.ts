import {ISeatPosition} from "../model/UserStatus";
import FirestoreUtil from "../util/FirestoreUtil";
import {SeatStatusType} from "../model/Seat";
import {logger} from "firebase-functions/v2";


class SeatStatusHandler {
    static reserveSeat(userId: string, seatPosition: ISeatPosition): Promise<boolean> {
        return FirestoreUtil.updateSeat(
            userId,
            seatPosition,
            SeatStatusType.Reserved,
            false,
            (seat) => {
                logger.debug(`Seat found : ${seat}`);
                if (seat === undefined) return false;
                else {
                    return seat.isAvailable &&
                        seat.currentUserId === undefined &&
                        seat.status === SeatStatusType.None;
                }
            }
        ).then(() => true);
    }

    static cancelReservation(userId: string, seatPosition: ISeatPosition): Promise<boolean> {
        return FirestoreUtil.updateSeat(
            undefined,
            seatPosition,
            SeatStatusType.None,
            true,
            (seat) => {
                logger.debug(`Seat found : ${seat}`);
                if (seat === undefined) return false;
                else {
                    return !seat.isAvailable &&
                        seat.currentUserId === userId &&
                        seat.status === SeatStatusType.Reserved;
                }
            }
        ).then(() => true);
    }

    static occupySeat(userId: string, seatPosition: ISeatPosition): Promise<boolean> {
        return FirestoreUtil.updateSeat(
            userId,
            seatPosition,
            SeatStatusType.Occupied,
            false,
            (seat) => {
                logger.debug(`Seat found : ${seat}`);
                if (seat === undefined) return false;
                else {
                    return !seat.isAvailable &&
                        seat.currentUserId === userId &&
                        seat.status === SeatStatusType.Reserved;
                }
            }
        ).then(() => true);
    }

    static stopUsingSeat(userId: string, seatPosition: ISeatPosition): Promise<boolean> {
        return FirestoreUtil.updateSeat(
            undefined,
            seatPosition,
            SeatStatusType.None,
            true,
            (seat) => {
                logger.debug(`Seat found : ${seat}`);
                if (seat === undefined) return false;
                else {
                    return !seat.isAvailable &&
                        seat.currentUserId === userId &&
                        [SeatStatusType.Occupied, SeatStatusType.Away].includes(seat.status);
                }
            }
        ).then(() => true);
    }

    static leaveSeat(userId: string, seatPosition: ISeatPosition): Promise<boolean> {
        return FirestoreUtil.updateSeat(
            userId,
            seatPosition,
            SeatStatusType.Away,
            false,
            (seat) => {
                logger.debug(`Seat found : ${seat}`);
                if (seat === undefined) return false;
                else {
                    return !seat.isAvailable &&
                        seat.currentUserId === userId &&
                        seat.status === SeatStatusType.Occupied;
                }
            }
        ).then(() => true);
    }

    static returnToSeat(userId: string, seatPosition: ISeatPosition): Promise<boolean> {
        return FirestoreUtil.updateSeat(
            userId,
            seatPosition,
            SeatStatusType.Occupied,
            false,
            (seat) => {
                logger.debug(`Seat found : ${seat}`);
                if (seat === undefined) return false;
                else {
                    return !seat.isAvailable &&
                        seat.currentUserId === userId &&
                        seat.status === SeatStatusType.Away;
                }
            }
        ).then(() => true);
    }

    static restrictSeat(seatPosition: ISeatPosition): Promise<boolean> {
        return FirestoreUtil.updateSeat(
            undefined,
            seatPosition,
            SeatStatusType.Restricted,
            false,
            (seat) => {
                logger.debug(`Seat found : ${seat}`);
                if (seat === undefined) return false;
                else {
                    return seat.isAvailable &&
                        seat.currentUserId === undefined &&
                        seat.status === SeatStatusType.None;
                }
            }
        ).then(() => true);
    }

    static unrestrictSeat(seatPosition: ISeatPosition): Promise<boolean> {
        return FirestoreUtil.updateSeat(
            undefined,
            seatPosition,
            SeatStatusType.None,
            true,
            (seat) => {
                logger.debug(`Seat found : ${seat}`);
                if (seat === undefined) return false;
                else {
                    return !seat.isAvailable &&
                        seat.currentUserId === undefined &&
                        seat.status === SeatStatusType.Restricted;
                }
            }
        ).then(() => true);
    }
}

export default SeatStatusHandler;

