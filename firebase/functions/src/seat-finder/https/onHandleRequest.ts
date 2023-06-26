import {CallableRequest, onCall} from "firebase-functions/v2/https";
import {getEndTime, ISeatFinderRequest} from "../_model/SeatFinderRequest";
import SeatFinderHandler from "../SeatFinderHandler";
import {https, logger} from "firebase-functions/v2";

// TODO: use parameterized configuration when fixed
const SEAT_FINDER_FUNCTION_LOCATION = "SEAT_FINDER_FUNCTION_LOCATION";

export const onHandleRequest =
    onCall<ISeatFinderRequest>(
        {
            region: "asia-northeast3",
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
