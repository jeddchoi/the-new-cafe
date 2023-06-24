import CloudTaskUtil from "./CloudTaskUtil";
import {defineString, projectID} from "firebase-functions/params";
import {logger} from "firebase-functions/v2";

const tasksLocation = defineString("SEAT_FINDER_TIMER_TASKS_LOCATION");
const tasksQueueName = defineString("SEAT_FINDER_TIMER_TASKS_QUEUE_NAME");

export default class SeatFinderTimerHandler {
    private static cloudTaskUtil: CloudTaskUtil;

    constructor() {
        logger.debug("[SeatFinderTimerHandler] constructor");
        SeatFinderTimerHandler.cloudTaskUtil = CloudTaskUtil.getInstance(
            projectID.value(),
            tasksLocation.value(),
            tasksQueueName.value(),
        );
    }

    sayHello() {
        logger.debug("sayHello");
    }
}
