import {logger, https} from "firebase-functions/v2";
import {Response} from "express";
import {projectID} from "firebase-functions/params";

export const helloWorldHandler = (
    request: https.Request,
    response: Response
) => {
    logger.info(`Timer Ended : ${new Date().toISOString()}`, {structuredData: JSON.stringify(request.body)},);
    response.send(`${projectID.value()}`);
};

