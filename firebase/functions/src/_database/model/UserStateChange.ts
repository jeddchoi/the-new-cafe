import {SeatFinderRequestType} from "../../seat-finder/_enum/SeatFinderRequestType";
import {UserStateChangeReason} from "../../seat-finder/_enum/UserStateChangeReason";
import {UserStateType} from "../../seat-finder/_enum/UserStateType";

export interface UserStateChange {
    requestType: SeatFinderRequestType;
    timestamp: number;
    resultState: UserStateType;
    reason: UserStateChangeReason;
    success: boolean;
}
