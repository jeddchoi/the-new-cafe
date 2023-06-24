import CloudTaskUtil from "./CloudTaskUtil";
import {defineString, projectID} from "firebase-functions/params";
import {logger} from "firebase-functions/v2";
import {SeatFinderRequestType} from "../seat-finder/_enum/SeatFinderRequestType";
import {TimerPayload} from "./TimerPayload";

const tasksLocation = defineString("SEAT_FINDER_TIMER_TASKS_LOCATION");
const tasksQueueName = defineString("SEAT_FINDER_TIMER_TASKS_QUEUE_NAME");

export default class SeatFinderTimerHandler {
    private static cloudTaskUtil: CloudTaskUtil;

    constructor() {
        logger.debug("[SeatFinderTimerHandler] constructor");
        // This should be called during cloud functions
        SeatFinderTimerHandler.cloudTaskUtil = CloudTaskUtil.getInstance(
            projectID.value(),
            tasksLocation.value(),
            tasksQueueName.value(),
        );
    }

    startTimer(
        timerName: string,
        userId: string,
        willRequestType: SeatFinderRequestType,
        endTime: number,
    ) {
        const url = "";
        return SeatFinderTimerHandler.cloudTaskUtil.createOneShotHttpPostTask(
            timerName,
            url,
            Math.round(endTime / 1000),
            <TimerPayload>{
                userId,
                requestType: willRequestType,
                endTime: endTime,
            }
        );
    }
    sayHello() {
        logger.debug("sayHello");
    }
}
