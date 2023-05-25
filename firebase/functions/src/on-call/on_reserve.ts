import {CallableRequest} from "firebase-functions/v2/https";
import {UserSeatUpdateRequest} from "../model/UserSeatUpdateRequest";
import {UserStatusChangeReason, UserStatusType} from "../model/UserStatus";
import {getEta, throwFunctionsHttpsError} from "../util/functions_helper";
import UserStatusHandler from "../handler/UserStatusHandler";
import {logger} from "firebase-functions/v2";
import SeatStatusHandler from "../handler/SeatStatusHandler";
import {CloudTasksUtil} from "../util/CloudTasksUtil";

export const reserveSeatHandler = (
    request: CallableRequest<UserSeatUpdateRequest>,
): Promise<boolean> => {
    logger.info("=================reserveSeat==================", {request: request.data});

    if (request.data.targetStatusType !== UserStatusType.Reserved) {
        throwFunctionsHttpsError("invalid-argument", `Wrong target status type : ${request.data.targetStatusType}`);
    }
    if (!request.data.seatPosition) {
        throwFunctionsHttpsError("invalid-argument", "Seat position is not provided");
    }
    // TODO: validate auth(not simulated)
    // if (!request.auth) {
    //     throwFunctionsHttpsError("unauthenticated", "User is not authenticated");
    // }

    const promises = [];

    const requestedAt = new Date().getTime();
    const eta = getEta(requestedAt, request.data.durationInSeconds, request.data.until);
    const seatPosition = request.data.seatPosition;
    const timer = new CloudTasksUtil();

    promises.push(SeatStatusHandler.reserveSeat(
        // request.auth?.uid,
        "87qDBiucwAaEbfV195l1vBTzeMVY",
        seatPosition,
    ));
    promises.push(timer.reserveUserSeatUpdate(
        <UserSeatUpdateRequest>{
            targetStatusType: UserStatusType.None,
            reason: UserStatusChangeReason.Timeout,
        },
        "/helloWorld",
        Math.round(eta / 1000), // eta in millisec
    ).then((task) => {
        if (!task.name) {
            throwFunctionsHttpsError("internal", "Timer task failed to start");
        }
        return UserStatusHandler.reserveSeat(
            // request.auth?.uid,
            "87qDBiucwAaEbfV195l1vBTzeMVY",
            seatPosition,
            requestedAt,
            eta,
            task.name
        );
    }));

    return Promise.all(promises).then((results) => {
        return results.every((result) => result);
    });
};

