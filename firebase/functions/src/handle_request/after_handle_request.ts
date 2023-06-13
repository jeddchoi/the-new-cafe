import {logger} from "firebase-functions/v2";
import {RequestType, resultStateByRequestType} from "../model/RequestType";
import UserSessionHandler from "../handler/UserSessionHandler";
import {UserStateChangeReason} from "../model/UserStateChangeReason";
import {SeatPosition} from "../model/SeatPosition";

export function afterRequestHandler(
    userId: string,
    requestType: RequestType,
    success: boolean,
    reason: UserStateChangeReason,
    seatPosition: SeatPosition | null,
    current: number = new Date().getTime(),
) {
    logger.debug(`afterRequestHandler : ${userId} ${requestType} ${success} ${reason} ${current} ${JSON.stringify(seatPosition)}`);
    const sessionHandler = new UserSessionHandler(userId);
    const resultState = resultStateByRequestType(requestType, success);
    switch (requestType) {
        case RequestType.ReserveSeat: {
            return sessionHandler.createSession(current, seatPosition).then(() => {
                return sessionHandler.addStateChange(requestType, resultState, current, reason, success);
            }).then(() => {
                if (!success) {
                    return sessionHandler.cleanupSession(requestType, current);
                } else return;
            });
        }
        case RequestType.CancelReservation:
        case RequestType.StopUsingSeat: {
            return sessionHandler.addStateChange(requestType, resultState, current, reason, success)
                .then(() => sessionHandler.cleanupSession(requestType, current));
        }
        default: {
            return sessionHandler.addStateChange(requestType, resultState, current, reason, success).then(() => {
                return;
            });
        }
    }
}
