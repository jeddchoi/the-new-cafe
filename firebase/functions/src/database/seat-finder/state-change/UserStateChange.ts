import {SeatFinderRequestType} from "../../../https/request/SeatFinderRequestType";
import {UserStateType} from "../UserStateType";
import {UserStateChangeReason} from "./UserStateChangeReason";

export interface UserStateChange {
    requestType: SeatFinderRequestType;
    timestamp: number;
    resultState: UserStateType;
    reason: UserStateChangeReason;
    success: boolean;
}
