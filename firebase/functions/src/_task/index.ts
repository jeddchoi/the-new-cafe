import {onRequest, Request} from "firebase-functions/lib/v2/providers/https";
import {Response} from "express";

export const onTest =
    onRequest((req: Request, res: Response) => Promise.resolve().then(() => {
        res.status(200);
    }));
