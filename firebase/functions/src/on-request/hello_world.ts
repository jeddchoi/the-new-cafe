import {logger, https} from "firebase-functions";
import {Response} from "express";
import {defineString, projectID} from "firebase-functions/params";

const taskQueueName = defineString("TASKS_QUEUE_NAME");
const taskLocation = defineString("MY_TASKS_LOCATION");
export const helloWorldHandler = (
    request: https.Request,
    response: Response
) => {
    logger.info("Hello logs!", {structuredData: true},);
    response.send(`Hello from Firebase! : ${projectID}`);
};

