import {TimerInfo} from "../../seat-finder/session/TimerInfo";
import {BlockReason} from "../../../enum/BlockReason";

export interface BlockInfo {
    reason: BlockReason,
    startTime: number;
    timer: TimerInfo | null;
}
