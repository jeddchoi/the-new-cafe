import {SeatFinderRequestType} from "../_enum/SeatFinderRequestType";
import {SeatPosition} from "../../_firestore/model/SeatPosition";
import "reflect-metadata";

export interface ISeatFinderRequest {
    requestType: SeatFinderRequestType;
    seatPosition: SeatPosition | null;
    durationInSeconds: number | null;
    endTime: number | null;
}

export function getEndTime(durationInSeconds : number | null, endTime: number | null, startTime: number) {
    if (durationInSeconds === null && endTime === null) {
        return null;
    } else if (durationInSeconds !== null) {
        return startTime + durationInSeconds * 1000;
    } else {
        return endTime;
    }
}
