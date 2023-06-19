import {SeatPosition} from "../../../firestore/seat/SeatPosition";
import {PartialUserState} from "./PartialUserState";

export interface CurrentSession {
    sessionId: string;
    seatPosition: SeatPosition;
    startSessionTime: number;
    main: PartialUserState;
    sub: PartialUserState | null;
}
