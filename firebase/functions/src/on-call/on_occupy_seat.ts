import {UserSeatUpdateRequest} from "../model/UserSeatUpdateRequest";
import {logger} from "firebase-functions/lib/v2";
import {UserStatusChangeReason, UserStatusType} from "../model/UserStatus";
import {getEta, throwFunctionsHttpsError} from "../util/functions_helper";
import CloudTasksUtil from "../util/CloudTasksUtil";
import SeatStatusHandler from "../handler/SeatStatusHandler";
import UserStatusHandler from "../handler/UserStatusHandler";


export function occupySeatHandler(request: UserSeatUpdateRequest): Promise<boolean> {
    logger.info("================= occupySeat ==================", {request: request});

    // Validate request
    if (request.targetStatusType !== UserStatusType.Occupied) {
        throwFunctionsHttpsError("invalid-argument", `Wrong target status type : ${request.targetStatusType}`);
    }
    if (!request.seatPosition) {
        throwFunctionsHttpsError("invalid-argument", "Seat position is not provided");
    }
    // TODO: validate auth(not simulated)
    // if (!request.auth) {
    //     throwFunctionsHttpsError("unauthenticated", "User is not authenticated");
    // }

    const promises: Promise<boolean>[] = [];

    const requestedAt = new Date().getTime();
    const eta = getEta(requestedAt, request.durationInSeconds, request.until);
    const seatPosition = request.seatPosition;
    const timer = new CloudTasksUtil();

    // 1. Handle seat status change
    promises.push(SeatStatusHandler.occupySeat(
        // request.auth?.uid,
        "sI2wbdRqYtdgArsq678BFSGDwr43",
        seatPosition,
    ));

    // 2. Start timer and handle user status change
    promises.push(timer.reserveUserSeatUpdate(
        new UserSeatUpdateRequest(
            // userId: request.auth?.uid,
            "sI2wbdRqYtdgArsq678BFSGDwr43",
            UserStatusType.None,
            UserStatusChangeReason.Timeout,
            seatPosition,
            undefined,
            eta
        ),
        "/timeoutOnReachUsageLimit",
        Math.round(eta / 1000), // eta in millisec
    ).then((task) => {
        if (!task.name) {
            throwFunctionsHttpsError("internal", "Timer task failed to start");
        }
        return UserStatusHandler.occupySeat(
            // request.auth?.uid,
            "sI2wbdRqYtdgArsq678BFSGDwr43",
            seatPosition,
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
