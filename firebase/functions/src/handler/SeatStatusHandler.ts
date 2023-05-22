import {StatusHandler} from "./StatusHandler";
import {ISeatPosition} from "../model/UserStatus";
import FirestoreUtil from "../util/FirestoreUtil";
import {SeatStatusType} from "../model/Seat";


const SeatStatusHandler: StatusHandler = class StatusHandler {
    static reserveSeat(userId: string, seatPosition: ISeatPosition): Promise<boolean> {
        return FirestoreUtil.updateSeat(seatPosition, SeatStatusType.Reserved, false, userId)
            .then(() => true);
    }
};

export default SeatStatusHandler;

