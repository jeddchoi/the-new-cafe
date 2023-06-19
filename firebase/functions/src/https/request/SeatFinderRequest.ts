import {SeatFinderRequestType} from "./SeatFinderRequestType";
import {SeatPosition} from "../../firestore/seat/SeatPosition";

export class SeatFinderRequest {
    constructor(
        readonly requestType: SeatFinderRequestType,
        readonly seatPosition: SeatPosition | null,
        readonly durationInSeconds: number | null,
        private readonly _endTime: number | null
    ) {
    }

    static fromJson(json: {
        requestType: SeatFinderRequestType,
        seatPosition: SeatPosition | null,
        durationInSeconds: number | null,
        endTime: number | null
    }): SeatFinderRequest {
        return new SeatFinderRequest(
            json.requestType,
            json.seatPosition,
            json.durationInSeconds,
            json.endTime
        );
    }

    endTime(startTime: number) {
        if (this.durationInSeconds === null && this._endTime === null) {
            return null;
        } else if (this.durationInSeconds !== null) {
            return startTime + this.durationInSeconds * 1000;
        } else {
            return this._endTime;
        }
    }
}
