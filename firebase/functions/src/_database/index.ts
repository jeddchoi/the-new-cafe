import {onRequest, Request} from "firebase-functions/v2/https";
import {Response} from "express";
import {https, logger} from "firebase-functions/v2";
import SessionHandler from "./SessionHandler";
import {SeatFinderRequestType} from "../seat-finder/_enum/SeatFinderRequestType";
import {SeatPosition} from "../_firestore/model/SeatPosition";
import {isResultCode} from "../helper/isResultCode";
import {getEndTime, ISeatFinderRequest} from "../seat-finder/_model/SeatFinderRequest";

export const onTest =
    onRequest((req: Request, res: Response) => Promise.resolve().then(() => {
        logger.log(`body = ${JSON.stringify(req.body)}`);
        const request = req.body as ISeatFinderRequest;
        logger.log(`request: ${JSON.stringify(request)}`);

        const existingSeatPos = <SeatPosition>{
            storeId: "i9sAij5mVBijR85hgraE",
            sectionId: "FMLYWLzKmiou1PTcrFR8",
            seatId: "ZlblGsMYd7IlO1DEho4H",
        };

        const sessionHandler = new SessionHandler("TEST_USER_ID");
        const current = new Date().getTime();

        switch (request.requestType) {
            case SeatFinderRequestType.ReserveSeat: {
                return sessionHandler.reserveSeat("NEW_SESSION_ID", existingSeatPos, current, getEndTime(request.durationInSeconds, request.endTime, current));
            }
            case SeatFinderRequestType.OccupySeat: {
                return sessionHandler.occupySeat(current, getEndTime(request.durationInSeconds, request.endTime, current));
            }
            case SeatFinderRequestType.LeaveAway: {
                return sessionHandler.leaveAway(current, getEndTime(request.durationInSeconds, request.endTime, current));
            }
            case SeatFinderRequestType.DoBusiness: {
                return sessionHandler.doBusiness(current, getEndTime(request.durationInSeconds, request.endTime, current));
            }
            case SeatFinderRequestType.ResumeUsing: {
                return sessionHandler.resumeUsing();
            }
            case SeatFinderRequestType.ChangeMainStateEndTime: {
                return sessionHandler.changeMainStateEndTime(current, getEndTime(request.durationInSeconds, request.endTime, current));
            }
            case SeatFinderRequestType.ChangeSubStateEndTime: {
                return sessionHandler.changeSubStateEndTime(current, getEndTime(request.durationInSeconds, request.endTime, current));
            }
            case SeatFinderRequestType.Quit: {
                return sessionHandler.quit();
            }
            default: {
                throw new https.HttpsError("unimplemented", "Not implemented.");
            }
        }
    }).then((result) => {
        logger.info("FINAL SUCCESS", result);
        res.status(200).send(result);
    }).catch(((err) => {
        logger.error(`FINAL ERROR = ${err}`);
        if (isResultCode(err)) {
            res.status(200).send({err});
        } else {
            res.status(500).send({err});
        }
    })));
