import {logger} from "firebase-functions/v2";
import {RequestType, resultStateByRequestType} from "../model/RequestType";
import UserSessionHandler from "../handler/UserSessionHandler";
import {UserStateChangeReason} from "../model/UserStateChangeReason";

export function afterRequestHandler(
    userId: string,
    requestType: RequestType,
    success: boolean,
    reason: UserStateChangeReason,
    current: number = new Date().getTime(),
) {
    logger.debug(`afterRequestHandler : ${userId} ${requestType} ${success} ${reason} ${current}`);
    const sessionHandler = new UserSessionHandler(userId);
    const resultState = resultStateByRequestType(requestType, success);
    switch (requestType) {
        case RequestType.CancelReservation:
        case RequestType.StopUsingSeat: {
            return sessionHandler.cleanupSession(requestType, current, reason, success);
        }
        default: {
            return sessionHandler.addStateChange(requestType, resultState, current, reason, success).then(() => {
                return;
            });
        }
    }
}
