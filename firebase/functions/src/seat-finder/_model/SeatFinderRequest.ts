import {SeatFinderRequestType} from "../_enum/SeatFinderRequestType";
import {SeatPosition} from "../../_firestore/model/SeatPosition";
import "reflect-metadata";

export interface ISeatFinderRequest {
    requestType: SeatFinderRequestType;
    seatPosition: SeatPosition | null;
    durationInSeconds: number | null;
    endTime: number | null;
}

export function getEndTime(request: ISeatFinderRequest, startTime: number) {
    if (request.durationInSeconds === null && request.endTime === null) {
        return null;
    } else if (request.durationInSeconds !== null) {
        return startTime + request.durationInSeconds * 1000;
    } else {
        return request.endTime;
    }
}
