import {SeatPosition} from "../../../firestore/seat/SeatPosition";

export interface PreviousSession {
    startTime: number;
    endTime: number;
    seatPosition: SeatPosition;
    hasError: boolean;
}
