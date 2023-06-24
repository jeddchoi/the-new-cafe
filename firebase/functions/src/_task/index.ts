import {onRequest, Request} from "firebase-functions/v2/https";
import {Response} from "express";
import SeatFinderTimer from "./SeatFinderTimer";

export const onTest =
    onRequest((req: Request, res: Response) => Promise.resolve().then(() => {
        const cloudFunctionName = "SeatFinder.onTimeout";
        const timer = new SeatFinderTimer(cloudFunctionName);
        timer.sayHello();
        res.status(200);
    }));
