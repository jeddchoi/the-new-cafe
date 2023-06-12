import CloudTasksUtil from "../util/CloudTasksUtil";
import {TemporaryTimerInfo} from "../model/UserState";
import {REFERENCE_USER_STATE_NAME} from "../util/RealtimeDatabaseUtil";
import {DatabaseEvent, DataSnapshot} from "firebase-functions/v2/database";
import {Change} from "firebase-functions/v2/firestore";
import {logger} from "firebase-functions/v2";

export const temporaryTimerWrittenHandler = (event: DatabaseEvent<Change<DataSnapshot>, { userId: string }>) => {
    logger.debug(`temporaryTimerWrittenHandler ${event.params.userId}`);
    const timer = new CloudTasksUtil();
    const promises = [];
    if (event.data.before.exists()) {
        const existingTimer = event.data.before.val() as TemporaryTimerInfo;
        promises.push(timer.cancelTimer(existingTimer.taskName));
    }

    if (event.data.after.exists()) {
        const newTimer = event.data.after.val() as TemporaryTimerInfo;
        promises.push(timer.startRemoveTimer(
            newTimer.isReset ?
                `/${REFERENCE_USER_STATE_NAME}/${event.params.userId}/status` :
                `/${REFERENCE_USER_STATE_NAME}/${event.params.userId}/status/temporary`,
            newTimer.endTime,
            newTimer.taskName));
    }

    return Promise.all(promises);
};
