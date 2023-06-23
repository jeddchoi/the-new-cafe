import {CallableRequest, onCall, onRequest, Request} from "firebase-functions/v2/https";
import {ISeatFinderRequest} from "./_model/SeatFinderRequest";
import SeatFinderHandler from "./SeatFinderHandler";
import {https, logger} from "firebase-functions/v2";
import {Response} from "express";

export const onHandleRequest =
    onCall<ISeatFinderRequest>((request: CallableRequest<ISeatFinderRequest>) => {
        if (!request.auth?.uid) {
            throw new https.HttpsError("unauthenticated", "User must be authenticated");
        }

        const handler = new SeatFinderHandler(request.auth.uid);
        logger.log("onHandleRequest =======", {request: request.data});
        return handler.handleSeatFinderRequest(request.data);
    });
// SeatFinder.onHandleRequest({seatPosition: {storeId: "i9sAij5mVBijR85hgraE", sectionId: "FMLYWLzKmiou1PTcrFR8", seatId: "ZlblGsMYd7IlO1DEho4H"}, durationInSeconds: 100, requestType: "ReserveSeat"})


export const onTimeout =
    onRequest((req: Request, res: Response) => Promise.resolve().then(() => {
        const request = req.body.request as ISeatFinderRequest;
        const userId = req.body.userId as string;
        const handler = new SeatFinderHandler(userId);
        logger.log("onTimeout =======", {request, userId});
        return handler.handleSeatFinderRequest(request, true).then((result) => {
            res.status(200).send(result);
        }).catch((err) => {
            res.status(500).send({err});
        });
    }));
