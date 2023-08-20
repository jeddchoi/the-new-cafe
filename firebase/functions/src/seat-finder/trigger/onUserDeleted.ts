import {auth} from "firebase-functions";
import {logger} from "firebase-functions/v2";
import SeatFinderHandler from "../SeatFinderHandler";
import {SeatFinderRequestType} from "../_enum/SeatFinderRequestType";
import {SeatFinderEventBy} from "../_enum/SeatFinderEventBy";

export const clearUserRecords = auth.user().onDelete(async (user) => {
    logger.debug(`[clearUserRecords] called ${JSON.stringify(user)}`);
    const current = Date.now();
    const handler = new SeatFinderHandler(user.uid);

    await handler.handleSeatFinderRequest(
        SeatFinderRequestType.Quit,
        current,
        null,
        null,
        SeatFinderEventBy.Admin);
    return await handler.clearAllRecords();
});
