import {TimerInfo} from "./TimerInfo";
import {UserStateType} from "../../seat-finder/_enum/UserStateType";

export interface PartialUserState {
    startTime: number;
    state: UserStateType;
    timer: TimerInfo | null;
}
