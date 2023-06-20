import {onRequest, Request} from "firebase-functions/v2/https";
import {Response} from "express";
import SeatHandler from "./handler/SeatHandler";
import {SeatPosition} from "./model/SeatPosition";
import {SeatFinderRequestType} from "../seat-finder/_enum/SeatFinderRequestType";
import {https, logger} from "firebase-functions/v2";
import {SeatFinderRequest} from "../seat-finder/_model/SeatFinderRequest";

export const onTest =
    onRequest((req: Request, res: Response) => Promise.resolve().then(() => {
        logger.log(`body = ${JSON.stringify(req.body)}`);
        const request = SeatFinderRequest.fromJson(req.body);
        logger.log(`request: ${JSON.stringify(request)}`);

        const existingSeatPos = new SeatPosition(
            "i9sAij5mVBijR85hgraE",
            "FMLYWLzKmiou1PTcrFR8",
            "ZlblGsMYd7IlO1DEho4H");

        const seatHandler = new SeatHandler("TEST_USER_ID");
        const current = new Date().getTime();

        switch (+request.requestType) {
            case SeatFinderRequestType.ReserveSeat: {
                return seatHandler.reserveSeat(existingSeatPos, request.getEndTime(current));
            }
            case SeatFinderRequestType.OccupySeat: {
                return seatHandler.occupySeat(existingSeatPos, request.getEndTime(current));
            }
            case SeatFinderRequestType.LeaveAway:
            case SeatFinderRequestType.ShiftToBusiness:
            case SeatFinderRequestType.DoBusiness:
                return seatHandler.away(existingSeatPos);
            case SeatFinderRequestType.ResumeUsing:
                return seatHandler.resumeUsing(existingSeatPos, request.getEndTime(current));
            case SeatFinderRequestType.ChangeMainStateEndTime:
                return seatHandler.changeReserveEndTime(existingSeatPos, request.getEndTime(current));
                // return seatHandler.changeOccupyEndTime(existingSeatPos, request.getEndTime(current));
            case SeatFinderRequestType.Quit:
                return seatHandler.freeSeat(existingSeatPos);
            default: {
                throw new https.HttpsError("unimplemented", "Not implemented.");
            }
        }
    }).then((result) => {
        logger.info("success", result);
        res.sendStatus(200);
    }).catch(((err) => {
        logger.error(err);
        res.status(500).send(err);
    })));

