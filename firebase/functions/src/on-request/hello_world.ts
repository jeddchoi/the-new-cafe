import {logger, https} from "firebase-functions/v2";
import {Response} from "express";
import {projectID} from "firebase-functions/params";
import {UserSeatUpdateRequest} from "../model/UserSeatUpdateRequest";

export const helloWorldHandler = (
    request: https.Request,
    response: Response
) => {
    logger.info(`Timer Ended : ${new Date().toISOString()}`, {structuredData: request.body as UserSeatUpdateRequest},);
    response.send(`${projectID.value()}`);
};

