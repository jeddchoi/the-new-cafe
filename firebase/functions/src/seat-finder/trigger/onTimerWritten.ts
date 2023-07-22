import {onValueWritten} from "firebase-functions/v2/database";
import {
    REFERENCE_CURRENT_SESSION_NAME,
    REFERENCE_SEAT_FINDER_NAME,
    REFERENCE_TIMER_NAME,
} from "../../_database/NameConstant";
import SeatFinderTimer from "../../_task/SeatFinderTimer";
import {TimerInfo} from "../../_database/model/TimerInfo";
import {logger} from "firebase-functions/v2";


let seatFinderTimer: SeatFinderTimer;
export const onTimerWritten =
    onValueWritten(
        {
            ref: `${REFERENCE_SEAT_FINDER_NAME}/{userId}/${REFERENCE_CURRENT_SESSION_NAME}/{stateType}/${REFERENCE_TIMER_NAME}`,
            region: "asia-southeast1",
        },
        (event) => {
            logger.debug(`[onTimerWritten] called ${JSON.stringify(event.data)}`);
            seatFinderTimer = seatFinderTimer ?? new SeatFinderTimer("SeatFinder-onTimeout");
            const promises = [];
            if (event.data.before.exists()) {
                const stoppedTimer = event.data.before.val() as TimerInfo;
                logger.debug(`[onTimerWritten] Stop timer ${JSON.stringify(stoppedTimer)}`);
                if (stoppedTimer.endTime > Date.now()) { // not expired yet
                    promises.push(seatFinderTimer.stopTimer(stoppedTimer.taskId));
                }
            }
            if (event.data.after.exists()) {
                const newTimer = event.data.after.val() as TimerInfo;
                logger.debug(`[onTimerWritten] Start timer ${JSON.stringify(newTimer)}`);
                promises.push(seatFinderTimer.startTimer(
                    newTimer.taskId,
                    event.params.userId,
                    newTimer.willRequestType,
                    newTimer.endTime,
                ));
            }
            return Promise.all(promises);
        }
    );
