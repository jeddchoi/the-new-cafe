import {UserSeatUpdateRequest} from "../model/UserSeatUpdateRequest";
import {logger} from "firebase-functions/lib/v2";
import {UserStatusChangeReason, UserStatusType} from "../model/UserStatus";
import {getEta, throwFunctionsHttpsError} from "../util/functions_helper";
import CloudTasksUtil from "../util/CloudTasksUtil";
import SeatStatusHandler from "../handler/SeatStatusHandler";
import UserStatusHandler from "../handler/UserStatusHandler";


export function goVacantHandler(request: UserSeatUpdateRequest): Promise<boolean> {
    logger.info("================= Go Vacant ==================", {request: request});

    // Validate request
    if (request.targetStatusType !== UserStatusType.Vacant) {
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
    promises.push(SeatStatusHandler.leaveSeat(
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
        "/timeoutOnVacant",
        Math.round(eta / 1000), // eta in millisec
    ).then((task) => {
        if (!task.name) {
            throwFunctionsHttpsError("internal", "Timer task failed to start");
        }
        return UserStatusHandler.goVacant(
            // request.auth?.uid,
            "sI2wbdRqYtdgArsq678BFSGDwr43",
            request.seatPosition,
            requestedAt,
            eta,
            task.name,
            request.reason,
        );
    }));

    // 3. TODO: handle user history update

    return Promise.all(promises).then((results) => {
        return results.every((result) => result);
    });
}
