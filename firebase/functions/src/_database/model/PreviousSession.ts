import {SeatPosition} from "../../_firestore/model/SeatPosition";
import "reflect-metadata";

export interface PreviousSession {
    readonly startTime: number;
    readonly endTime: number;
    readonly seatPosition: SeatPosition;
    readonly hasFailure: boolean;
}
