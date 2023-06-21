import {onRequest, Request} from "firebase-functions/v2/https";
import {Response} from "express";
import {https, logger} from "firebase-functions/v2";
import SessionHandler from "./handler/SessionHandler";
import {SeatFinderRequestType} from "../seat-finder/_enum/SeatFinderRequestType";
import {SeatPosition} from "../_firestore/model/SeatPosition";
import {SeatFinderRequest} from "../seat-finder/_model/SeatFinderRequest";
import {isResultCode} from "../helper/isResultCode";

export const onTest =
    onRequest((req: Request, res: Response) => Promise.resolve().then(() => {
        logger.log(`body = ${JSON.stringify(req.body)}`);
        const request = SeatFinderRequest.fromJson(req.body);
        logger.log(`request: ${JSON.stringify(request)}`);

        const existingSeatPos = new SeatPosition(
            "i9sAij5mVBijR85hgraE",
            "FMLYWLzKmiou1PTcrFR8",
            "ZlblGsMYd7IlO1DEho4H");

        const sessionHandler = new SessionHandler("TEST_USER_ID");
        const current = new Date().getTime();

        switch (+request.requestType) {
            case SeatFinderRequestType.ReserveSeat: {
                return sessionHandler.reserveSeat("NEW_SESSION_ID", existingSeatPos, current, request.getEndTime(current));
            }
            case SeatFinderRequestType.OccupySeat: {
                return sessionHandler.occupySeat(current, request.getEndTime(current));
            }
            case SeatFinderRequestType.LeaveAway: {
                return sessionHandler.leaveAway(current, request.getEndTime(current));
            }
            case SeatFinderRequestType.DoBusiness: {
                return sessionHandler.doBusiness(current, request.getEndTime(current));
            }
            case SeatFinderRequestType.ResumeUsing: {
                return sessionHandler.resumeUsing();
            }
            case SeatFinderRequestType.ChangeMainStateEndTime: {
                return sessionHandler.changeMainStateEndTime(current, request.getEndTime(current));
            }
            case SeatFinderRequestType.ChangeSubStateEndTime: {
                return sessionHandler.changeSubStateEndTime(current, request.getEndTime(current));
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
        res.sendStatus(200);
    }).catch(((err) => {
        logger.error(`FINAL ERROR = ${err}`);
        if (isResultCode(err)) {
            res.status(200).send({err});
        } else {
            res.status(500).send({err});
        }
    })));
