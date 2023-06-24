import {SeatFinderRequestType} from "../seat-finder/_enum/SeatFinderRequestType";

export interface TimerPayload {
    userId: string;
    requestType: SeatFinderRequestType;
    endTime: number;
}
