import {SeatFinderRequestType} from "../../../enum/SeatFinderRequestType";

export interface TimerInfo {
    willRequestType: SeatFinderRequestType | "unblock";
    endTime: number;
    taskName: string;
}
