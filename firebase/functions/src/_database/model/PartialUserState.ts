import {TimerInfo} from "./TimerInfo";

export interface PartialUserState<T> {
    startTime: number;
    state: T;
    timer: TimerInfo | null;
}
