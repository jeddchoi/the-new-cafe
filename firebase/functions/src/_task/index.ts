import {onRequest, Request} from "firebase-functions/v2/https";
import {Response} from "express";
import SeatFinderTimerHandler from "./SeatFinderTimerHandler";

export const onTest =
    onRequest((req: Request, res: Response) => Promise.resolve().then(() => {
        const timer = new SeatFinderTimerHandler();
        timer.sayHello();
        res.status(200);
    }));
