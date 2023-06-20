import {SeatPosition} from "../../_firestore/model/SeatPosition";

export interface PreviousSession {
    startTime: number;
    endTime: number;
    seatPosition: SeatPosition;
    hasError: boolean;
}
