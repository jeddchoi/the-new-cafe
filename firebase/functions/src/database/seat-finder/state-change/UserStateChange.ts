import {SeatFinderRequestType} from "../../../enum/SeatFinderRequestType";
import {UserStateType} from "../../../enum/UserStateType";
import {UserStateChangeReason} from "../../../enum/UserStateChangeReason";

export interface UserStateChange {
    requestType: SeatFinderRequestType;
    timestamp: number;
    resultState: UserStateType;
    reason: UserStateChangeReason;
    success: boolean;
}
