import {ISeatPosition} from "../model/UserStatus";
import FirestoreUtil from "../util/FirestoreUtil";
import {SeatStatusType} from "../model/Seat";


class SeatStatusHandler {
    static reserveSeat(userId: string, seatPosition: ISeatPosition): Promise<boolean> {
        return FirestoreUtil.updateSeat(seatPosition, SeatStatusType.Reserved, false, userId)
            .then(() => true);
    }

    static cancelReservation(userId: string, seatPosition: ISeatPosition): Promise<boolean> {
        return FirestoreUtil.updateSeat(seatPosition, SeatStatusType.None, true, userId).then(() => true);
    }

    static occupySeat(userId: string, seatPosition: ISeatPosition): Promise<boolean> {
        return FirestoreUtil.updateSeat(seatPosition, SeatStatusType.Occupied, false, userId).then(() => true);
    }

    static stopUsingSeat(userId: string, seatPosition: ISeatPosition): Promise<boolean> {
        return FirestoreUtil.updateSeat(seatPosition, SeatStatusType.None, true, userId).then(() => true);
    }

    static leaveSeat(userId: string, seatPosition: ISeatPosition): Promise<boolean> {
        return FirestoreUtil.updateSeat(seatPosition, SeatStatusType.Away, false, userId).then(() => true);
    }

    static returnToSeat(userId: string, seatPosition: ISeatPosition): Promise<boolean> {
        return FirestoreUtil.updateSeat(seatPosition, SeatStatusType.Occupied, false, userId).then(() => true);
    }

    static restrictSeat(seatPosition: ISeatPosition): Promise<boolean> {
        return FirestoreUtil.updateSeat(seatPosition, SeatStatusType.Restricted, false, undefined).then(()=> true);
    }

    static unrestrictSeat(seatPosition: ISeatPosition): Promise<boolean> {
        return FirestoreUtil.updateSeat(seatPosition, SeatStatusType.None, false, undefined).then(()=> true);
    }
}

export default SeatStatusHandler;

