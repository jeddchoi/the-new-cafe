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
}

export default SeatStatusHandler;

