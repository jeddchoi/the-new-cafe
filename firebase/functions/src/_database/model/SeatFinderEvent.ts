import {SeatFinderRequestType} from "../../seat-finder/_enum/SeatFinderRequestType";
import {SeatFinderEventBy} from "../../seat-finder/_enum/SeatFinderEventBy";
import {UserStateType} from "../../seat-finder/_enum/UserStateType";
import {SeatStateType} from "../../seat-finder/_enum/SeatStateType";
import "reflect-metadata";

export interface SeatFinderEvent {
    readonly requestType: SeatFinderRequestType;
    readonly timestamp: number;
    readonly reason: SeatFinderEventBy;
    readonly resultUserState: UserStateType;
    readonly resultSeatState: SeatStateType;
    readonly success: boolean;
}
