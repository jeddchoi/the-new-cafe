import {ISeatPosition} from "../model/UserStatus";


export interface StatusHandler {
    reserveSeat(userId: string, seatPosition: ISeatPosition): Promise<boolean>;
}
