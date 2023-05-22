import {StatusHandler} from "./StatusHandler";
import {ISeatPosition} from "../model/UserStatus";


const SeatStatusHandler: StatusHandler = class StatusHandler {
    static reserveSeat(userId: string, seatPosition: ISeatPosition): Promise<boolean> {
        return Promise.resolve(true);
    }
};

export default SeatStatusHandler;

