import CloudTasksUtil from "../util/CloudTasksUtil";
import {TimerInfo} from "../model/UserState";
import {REFERENCE_USER_STATE_NAME} from "../util/RealtimeDatabaseUtil";
import {DatabaseEvent, DataSnapshot} from "firebase-functions/v2/database";
import {logger} from "firebase-functions/v2";
import {RequestType} from "../model/RequestType";

export const temporaryTimerCreatedHandler = (event: DatabaseEvent<DataSnapshot, { userId: string }>) => {
    logger.debug(`temporaryTimerWrittenHandler ${event.params.userId}`);
    const timer = new CloudTasksUtil();
    const promises = [];
    // if (event.data.before.exists()) {
    //     const existingTimer = event.data.before.val() as TimerInfo;
    //     if (existingTimer.endTime > Date.now()) {
    //         promises.push(timer.cancelTimer(existingTimer.taskName));
    //     }
    // }

    if (event.data.exists()) {
        const newTimer = event.data.val() as TimerInfo;
        promises.push(timer.startRemoveTimer(
            event.params.userId,
            newTimer.willRequestType,
            newTimer.endTime,
            newTimer.taskName));
    }

    return Promise.all(promises);
};
