import {RequestType} from "./RequestType";
import {SeatPosition} from "./SeatPosition";

export class UserActionRequest {
    constructor(
        readonly requestType: RequestType,
        readonly seatPosition: SeatPosition | null,
        // if both endTime and durationInSeconds are null, no deadline
        readonly durationInSeconds : number | null,
        readonly endTime: number | null,
    ) {
    }

    getEndTime(startTime: number): number | null {
        if (this.durationInSeconds === null && this.endTime === null) {
            return null;
        } else if (this.durationInSeconds !== null) {
            return startTime + this.durationInSeconds * 1000;
        } else {
            return this.endTime;
        }
    }
}
