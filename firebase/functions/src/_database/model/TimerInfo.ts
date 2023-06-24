import {SeatFinderRequestType} from "../../seat-finder/_enum/SeatFinderRequestType";

export interface TimerInfo {
    willRequestType: SeatFinderRequestType;
    endTime: number;
    taskId: string;
}
