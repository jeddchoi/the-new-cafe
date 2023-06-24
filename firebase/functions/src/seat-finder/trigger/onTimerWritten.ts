import {onValueWritten} from "firebase-functions/v2/database";
import {
    REFERENCE_CURRENT_SESSION_NAME,
    REFERENCE_SEAT_FINDER_NAME,
    REFERENCE_TIMER_NAME,
} from "../../_database/NameConstant";
import SeatFinderTimer from "../../_task/SeatFinderTimer";
import {TimerInfo} from "../../_database/model/TimerInfo";


let seatFinderTimer: SeatFinderTimer;
export const onTimerWritten =
    onValueWritten(
        {
            ref: `${REFERENCE_SEAT_FINDER_NAME}/${REFERENCE_CURRENT_SESSION_NAME}/{userId}/{stateType}/${REFERENCE_TIMER_NAME}`,
            region: "asia-southeast1",
        },
        (event) => {
            seatFinderTimer = seatFinderTimer ?? new SeatFinderTimer("SeatFinder.onTimeout");
            const promises = [];
            if (event.data.before.exists()) {
                const stoppedTimer = event.data.before.val() as TimerInfo;
                if (stoppedTimer.endTime > Date.now()) { // not expired yet
                    promises.push(seatFinderTimer.stopTimer(stoppedTimer.taskId));
                }
            }
            if (event.data.after.exists()) {
                const newTimer = event.data.after.val() as TimerInfo;
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
