import {SeatFinderRequestType} from "../../seat-finder/_enum/SeatFinderRequestType";
import {UserStateType} from "../../seat-finder/_enum/UserStateType";
import {UserStateChangeReason} from "../../seat-finder/_enum/UserStateChangeReason";

export interface UserStateChange {
    requestType: SeatFinderRequestType;
    timestamp: number;
    resultState: UserStateType;
    reason: UserStateChangeReason;
    success: boolean;
}
