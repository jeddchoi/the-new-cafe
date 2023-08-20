import {auth} from "firebase-functions";
import {logger} from "firebase-functions/lib/v2";
import SeatFinderHandler from "../SeatFinderHandler";
import {SeatFinderRequestType} from "../_enum/SeatFinderRequestType";
import {SeatFinderEventBy} from "../_enum/SeatFinderEventBy";

export const clearUserRecords = auth.user().onDelete((user) => {
    logger.debug(`[clearUserHistories] called ${JSON.stringify(user)}`);
    const current = Date.now();
    const handler = new SeatFinderHandler(user.uid);

    return handler.handleSeatFinderRequest(
        SeatFinderRequestType.Quit,
        current,
        null,
        null,
        SeatFinderEventBy.Admin,
    ).then(() => {
        return handler.clearAllRecords();
    });
});
