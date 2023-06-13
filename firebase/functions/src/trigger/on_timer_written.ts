import {DatabaseEvent, DataSnapshot} from "firebase-functions/v2/database";
import CloudTasksUtil from "../util/CloudTasksUtil";
import {TimerInfo} from "../model/UserState";
import {logger} from "firebase-functions/v2";
import {Change} from "firebase-functions/v2/firestore";

export const timerWrittenHandler = (event: DatabaseEvent<Change<DataSnapshot>, { userId: string }>) => {
    logger.debug(`timerWrittenHandler: ${event.params.userId}`);
    const timer = new CloudTasksUtil();
    const promises = [];
    if (event.data.before.exists()) {
        const existingTimer = event.data.before.val() as TimerInfo;
        if (existingTimer.endTime > Date.now()) {
            promises.push(timer.cancelTimer(existingTimer.taskName));
        }
    }

    if (event.data.after.exists()) {
        const newTimer = event.data.after.val() as TimerInfo;
        promises.push(timer.startTimeoutTimer(
            event.params.userId,
            newTimer.willRequestType,
            newTimer.endTime,
            newTimer.taskName
        ));
    }

    return Promise.all(promises);
};
