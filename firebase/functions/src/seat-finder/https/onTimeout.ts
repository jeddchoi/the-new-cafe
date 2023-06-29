import {onRequest, Request} from "firebase-functions/v2/https";
import {Response} from "express";
import {TimerPayload} from "../../_task/TimerPayload";
import SeatFinderHandler from "../SeatFinderHandler";
import {logger} from "firebase-functions/v2";
import {defineList} from "firebase-functions/params";

const SEAT_FINDER_FUNCTION_LOCATION: unknown = defineList("SEAT_FINDER_FUNCTION_LOCATION");


export const onTimeout =
    onRequest(
        {
            region: <string[]>SEAT_FINDER_FUNCTION_LOCATION,
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
