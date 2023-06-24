import {onValueWritten} from "firebase-functions/lib/v2/providers/database";
import {
    REFERENCE_CURRENT_SESSION_NAME,
    REFERENCE_SEAT_FINDER_NAME,
    REFERENCE_TIMER_NAME,
} from "../../_database/NameConstant";

export const onTimerWritten =
    onValueWritten(
        {
            ref: `${REFERENCE_SEAT_FINDER_NAME}/${REFERENCE_CURRENT_SESSION_NAME}/{userId}/{stateType}/${REFERENCE_TIMER_NAME}`,
            region: "asia-southeast1",
        },
        (event) => {
            return Promise.resolve();
        }
    );
