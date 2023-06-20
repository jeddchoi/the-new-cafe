import {SeatFinderRequestType} from "../../seat-finder/_enum/SeatFinderRequestType";

export interface TimerInfo {
    willRequestType: SeatFinderRequestType | "unblock";
    endTime: number;
    taskName: string;
}
