import {onRequest, Request} from "firebase-functions/lib/v2/providers/https";
import {Response} from "express";
import SessionHandler from "./handler/SessionHandler";
import StateChangeHandler from "./handler/StateChangeHandler";
import SeatHandler from "../_firestore/handler/SeatHandler";
import {logger} from "firebase-functions/lib/v2";
import {SeatPosition} from "../_firestore/model/SeatPosition";

export const onTest =
    onRequest((req: Request, res: Response) => Promise.resolve().then(() => {
        const userId = "USER_ID";
        const seatPosition = <SeatPosition> {};
        const stateChangeHandler = new StateChangeHandler(userId);
        const sessionHandler = new SessionHandler(userId);
        const seatHandler = new SeatHandler(userId);
        const promise = stateChangeHandler.createSession(
            () => seatHandler.reserveSeat(seatPosition),
            (sessionId: string) => sessionHandler.reserveSeat(sessionId, seatPosition, 0),
        );
        const promise2 = stateChangeHandler.transaction(
            () => sessionHandler.occupySeat(0),
            (seatPosition) => seatHandler.occupySeat(seatPosition),
        );
    }).then((result) => {
        logger.info("success", result);
        res.sendStatus(200);
    }).catch(((err) => {
        logger.error(err);
        res.status(500).send(err);
    })));
