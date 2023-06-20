import {CallableRequest, onCall} from "firebase-functions/v2/https";
import {SeatFinderRequest} from "./_model/SeatFinderRequest";

export const onSeatFinderRequest =
    onCall<SeatFinderRequest>((request: CallableRequest<SeatFinderRequest>) => {
        return request.data;
    });
