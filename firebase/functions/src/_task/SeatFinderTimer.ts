import CloudTaskUtil from "./CloudTaskUtil";
import {defineString, projectID} from "firebase-functions/params";
import {logger} from "firebase-functions/v2";
import {SeatFinderRequestType} from "../seat-finder/_enum/SeatFinderRequestType";
import {TimerPayload} from "./TimerPayload";

const seatFinderTimerTasksLocation = defineString("SEAT_FINDER_TIMER_TASKS_LOCATION");
const tasksQueueName = defineString("SEAT_FINDER_TIMER_TASKS_QUEUE_NAME");
const seatFinderFunctionLocation = defineString("SEAT_FINDER_FUNCTION_LOCATION");

export default class SeatFinderTimer {
    private static cloudTaskUtil: CloudTaskUtil;
    private readonly invokeUrl:string;

    constructor(
        cloudFunctionName: string,
    ) {
        this.invokeUrl = `https://${seatFinderFunctionLocation.value()}-${projectID.value()}.cloudfunctions.net/${cloudFunctionName}`;
        logger.debug("[SeatFinderTimerHandler] constructor");
        // This should be called during cloud functions
        SeatFinderTimer.cloudTaskUtil = CloudTaskUtil.getInstance(
            projectID.value(),
            seatFinderTimerTasksLocation.value(),
            tasksQueueName.value(),
        );
    }

    startTimer(
        taskId: string,
        userId: string,
        willRequestType: SeatFinderRequestType,
        endTime: number,
    ) {
        return SeatFinderTimer.cloudTaskUtil.createOneShotHttpPostTask(
            taskId,
            this.invokeUrl,
            Math.round(endTime / 1000),
            <TimerPayload>{
                userId,
                requestType: willRequestType,
                endTime: endTime,
            }
        );
    }

    stopTimer(taskId: string) {
        return SeatFinderTimer.cloudTaskUtil.cancelTask(taskId);
    }
    sayHello() {
        logger.debug("sayHello");
    }
}
