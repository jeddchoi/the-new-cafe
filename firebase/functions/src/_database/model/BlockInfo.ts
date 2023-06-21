import {TimerInfo} from "./TimerInfo";
import {BlockReason} from "../../seat-finder/_enum/BlockReason";

export interface BlockInfo {
    reason: BlockReason,
    startTime: number;
    timer: Omit<TimerInfo, "willRequestType"> | null;
}
