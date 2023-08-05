import {SeatFinderRequestType} from "../_enum/SeatFinderRequestType";
import {SeatPosition} from "../../_firestore/model/SeatPosition";
import "reflect-metadata";

export interface ISeatFinderRequest {
    requestType: SeatFinderRequestType;
    seatPosition: SeatPosition | null;
    durationInSeconds: number | null;
    endTime: number | null;
}

export function getEndTime(startTime: number, durationInSeconds: number | null, endTime: number | null = null) {
    if (durationInSeconds === null && endTime === null) {
        return null;
    } else if (durationInSeconds !== null) {
        return startTime + durationInSeconds * 1000;
    } else {
        return endTime;
    }
}
