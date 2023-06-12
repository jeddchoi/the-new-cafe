import {DatabaseEvent, DataSnapshot} from "firebase-functions/v2/database";
import {Change} from "firebase-functions/v2/firestore";
import CloudTasksUtil from "../util/CloudTasksUtil";
import {TimerInfo} from "../model/UserState";
import {REFERENCE_USER_STATE_NAME} from "../util/RealtimeDatabaseUtil";
import {logger} from "firebase-functions/v2";

export const overallTimerWrittenHandler = (event: DatabaseEvent<Change<DataSnapshot>, { userId: string }>) => {
    logger.debug(`overallTimerWrittenHandler: ${event.params.userId}`);
    const timer = new CloudTasksUtil();
    const promises = [];
    if (event.data.before.exists()) {
        const existingTimer = event.data.before.val() as TimerInfo;
        promises.push(timer.cancelTimer(existingTimer.taskName));
    }

    if (event.data.after.exists()) {
        const newTimer = event.data.after.val() as TimerInfo;
        promises.push(timer.startRemoveTimer(`/${REFERENCE_USER_STATE_NAME}/${event.params.userId}/status`, newTimer.endTime, newTimer.taskName));
    }

    return Promise.all(promises);
};
