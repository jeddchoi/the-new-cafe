import {logger, https} from "firebase-functions/v2";
import {Response} from "express";
import {projectID} from "firebase-functions/params";
import {UserActionRequest} from "../model/request/UserActionRequest";


export const helloWorldHandler = (
    request: https.Request,
    response: Response
) => {
    const data = UserActionRequest.fromPaylod(request.body);
    logger.info(`Hello World! : ${data.toString()}`);
    response.send(`${projectID.value()}`);
};

