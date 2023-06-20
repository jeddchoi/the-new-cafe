import {TimerInfo} from "./TimerInfo";
import {UserStateType} from "../../../enum/UserStateType";

export interface PartialUserState {
    startTime: number;
    state: UserStateType;
    timer: TimerInfo | null;
}
