import {CallableRequest} from "firebase-functions/v2/https";
import {UserSeatUpdateRequest} from "../model/UserSeatUpdateRequest";
import {UserStatusType} from "../model/UserStatus";
import {throwFunctionsHttpsError} from "../util/functions_helper";
import UserStatusHandler from "../handler/UserStatusHandler";
import {logger} from "firebase-functions";

export const reserveSeatHandler = (
    request: CallableRequest<UserSeatUpdateRequest>,
): Promise<boolean> => {
    logger.info("reserveSeat", {request});

    if (request.data.targetStatusType === UserStatusType.Reserved) {
        throwFunctionsHttpsError("invalid-argument", `Wrong status type : ${request.data.targetStatusType}`);
    }
    if (!request.data.seatPosition) {
        throwFunctionsHttpsError("invalid-argument", "Seat position is not provided");
    }
    if (!request.data.until && !request.data.durationInSeconds) {
        throwFunctionsHttpsError("invalid-argument", "Until or durationInSeconds is not provided");
    }
    if (!request.auth) {
        throwFunctionsHttpsError("unauthenticated", "User is not authenticated");
    }

    const promises = [];

    promises.push(UserStatusHandler.reserveSeat(
        request.auth?.uid,
        request.data.seatPosition,
    ));

    return Promise.all(promises).then((results) => {
        return results.every((result) => result);
    });
};

