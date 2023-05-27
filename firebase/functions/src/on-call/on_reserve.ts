import {logger} from "firebase-functions/v2";
import {UserSeatUpdateRequest} from "../model/UserSeatUpdateRequest";
import {UserStatusChangeReason, UserStatusType} from "../model/UserStatus";
import {getEta, throwFunctionsHttpsError} from "../util/functions_helper";
import CloudTasksUtil from "../util/CloudTasksUtil";
import UserStatusHandler from "../handler/UserStatusHandler";
import SeatStatusHandler from "../handler/SeatStatusHandler";

export function reserveSeatHandler(request: UserSeatUpdateRequest): Promise<boolean> {
    logger.info("================= reserveSeat ==================", {request: request});

    // Validate request
    if (request.targetStatusType !== UserStatusType.Reserved) {
        throwFunctionsHttpsError("invalid-argument", `Wrong target status type : ${request.targetStatusType}`);
    }
    // TODO: validate auth(not simulated)
    // if (!request.auth) {
    //     throwFunctionsHttpsError("unauthenticated", "User is not authenticated");
    // }

    const promises: Promise<boolean>[] = [];

    const requestedAt = new Date().getTime();
    const eta = getEta(requestedAt, request.durationInSeconds, request.until);
    const timer = new CloudTasksUtil();

    // 1. Handle seat status change
    promises.push(SeatStatusHandler.reserveSeat(
        // request.auth?.uid,
        "sI2wbdRqYtdgArsq678BFSGDwr43",
        request.seatPosition,
    ));

    // 2. Start timer and handle user status change
    promises.push(timer.reserveUserSeatUpdate(
        new UserSeatUpdateRequest(
            // userId: request.auth?.uid,
            "sI2wbdRqYtdgArsq678BFSGDwr43",
            UserStatusType.None,
            UserStatusChangeReason.Timeout,
            request.seatPosition,
            undefined,
            eta
        ),
        "/timeoutOnReserve",
        Math.round(eta / 1000), // eta in millisec
    ).then((task) => {
        if (!task.name) {
            throwFunctionsHttpsError("internal", "Timer task failed to start");
        }
        return UserStatusHandler.reserveSeat(
            // request.auth?.uid,
            "sI2wbdRqYtdgArsq678BFSGDwr43",
            request.seatPosition,
            requestedAt,
            eta,
            task.name
        );
    }));

    // 3. TODO: handle user history update

    return Promise.all(promises).then((results) => {
        return results.every((result) => result);
    });
}
