import {onRequest, Request} from "firebase-functions/v2/https";
import {Response} from "express";
import SeatHandler from "./SeatHandler";
import {SeatPosition} from "./model/SeatPosition";
import {SeatFinderRequestType} from "../seat-finder/_enum/SeatFinderRequestType";
import {https, logger} from "firebase-functions/v2";
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

        const seatHandler = new SeatHandler("TEST_USER_ID");
        const current = new Date().getTime();

        switch (request.requestType) {
            case SeatFinderRequestType.ReserveSeat: {
                return seatHandler.reserveSeat(existingSeatPos, getEndTime(request, current));
            }
            case SeatFinderRequestType.OccupySeat: {
                return seatHandler.occupySeat(existingSeatPos, getEndTime(request, current));
            }
            case SeatFinderRequestType.LeaveAway:
            case SeatFinderRequestType.DoBusiness:
                return seatHandler.away(existingSeatPos);
            case SeatFinderRequestType.ResumeUsing:
                return seatHandler.resumeUsing(existingSeatPos);
            case SeatFinderRequestType.ChangeMainStateEndTime:
                // return seatHandler.changeReserveEndTime(existingSeatPos, request.getETA(current));
                return seatHandler.changeOccupyEndTime(existingSeatPos, getEndTime(request, current));
            case SeatFinderRequestType.Quit:
                return seatHandler.freeSeat(existingSeatPos);
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

