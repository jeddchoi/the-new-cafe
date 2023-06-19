import {SeatFinderRequestType} from "../../../https/request/SeatFinderRequestType";

export interface TimerInfo {
    willRequestType: SeatFinderRequestType | "unblock";
    endTime: number;
    taskName: string;
}
