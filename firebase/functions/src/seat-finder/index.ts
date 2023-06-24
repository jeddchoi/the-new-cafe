import {CallableRequest, onCall, onRequest, Request} from "firebase-functions/v2/https";
import {getEndTime, ISeatFinderRequest} from "./_model/SeatFinderRequest";
import SeatFinderHandler from "./SeatFinderHandler";
import {logger} from "firebase-functions/v2";
import {Response} from "express";
import {TimerPayload} from "../_task/TimerPayload";
import {defineString} from "firebase-functions/params";

const SEAT_FINDER_FUNCTION_LOCATION = "SEAT_FINDER_FUNCTION_LOCATION";

export const onHandleRequest =
    onCall<ISeatFinderRequest>(
        {
            region: defineString(SEAT_FINDER_FUNCTION_LOCATION),
        },
        (request: CallableRequest<ISeatFinderRequest>) => {
            // if (!request.auth?.uid) {
            //     throw new https.HttpsError("unauthenticated", "User must be authenticated");
            // }
            //
            // const handler = new SeatFinderHandler(request.auth.uid);
            const current = Date.now();
            const handler = new SeatFinderHandler("SAMPLE_USER_ID");
            logger.log("onHandleRequest =======", {request: request.data});
            return handler.handleSeatFinderRequest(
                request.data.requestType,
                current,
                getEndTime(request.data.durationInSeconds, request.data.endTime, current),
                request.data.seatPosition,
            );
        });
// SeatFinder.onHandleRequest({seatPosition: {storeId: "i9sAij5mVBijR85hgraE", sectionId: "FMLYWLzKmiou1PTcrFR8", seatId: "ZlblGsMYd7IlO1DEho4H"}, durationInSeconds: 100, requestType: "ReserveSeat"})


export const onTimeout =
    onRequest(
        {
            region: defineString(SEAT_FINDER_FUNCTION_LOCATION),
        },
        (req: Request, res: Response) => Promise.resolve().then(() => {
            const current = Date.now();
            const request = req.body as TimerPayload;
            const handler = new SeatFinderHandler(request.userId);
            logger.log("onTimeout =======", {request});
            return handler.handleSeatFinderRequest(
                request.requestType,
                current,
                request.endTime,
                null,
                true
            ).then((result) => {
                res.status(200).send(result);
            }).catch((err) => {
                res.status(500).send({err});
            });
        }));
