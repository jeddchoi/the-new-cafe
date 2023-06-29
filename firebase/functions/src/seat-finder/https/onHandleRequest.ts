import {CallableRequest, onCall} from "firebase-functions/v2/https";
import {getEndTime, ISeatFinderRequest} from "../_model/SeatFinderRequest";
import SeatFinderHandler from "../SeatFinderHandler";
import {https, logger} from "firebase-functions/v2";
import {defineList} from "firebase-functions/params";

const SEAT_FINDER_FUNCTION_LOCATION: unknown = defineList("SEAT_FINDER_FUNCTION_LOCATION");

export const onHandleRequest =
    onCall<ISeatFinderRequest>(
        {
            region: <string[]>SEAT_FINDER_FUNCTION_LOCATION,
        },
        (request: CallableRequest<ISeatFinderRequest>) => {
            if (!request.auth?.uid) {
                throw new https.HttpsError("unauthenticated", "User must be authenticated");
            }

            const current = Date.now();
            const handler = new SeatFinderHandler(request.auth.uid);
            logger.log("onHandleRequest =======", {request: request.data});

            logger.log(`isNull = ${request.data.endTime === null} ${request.data.endTime == null} ${request.data.endTime}`);
            return handler.handleSeatFinderRequest(
                request.data.requestType,
                current,
                getEndTime(request.data.durationInSeconds, request.data.endTime, current),
                request.data.seatPosition,
            );
        });
