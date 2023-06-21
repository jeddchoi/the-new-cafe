import {SeatPosition} from "../../_firestore/model/SeatPosition";
import {PartialUserState} from "./PartialUserState";

export interface CurrentSession {
    sessionId: string;
    seatPosition: SeatPosition;
    startSessionTime: number;
    mainState: PartialUserState;
    subState: PartialUserState | null;
}
