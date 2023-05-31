import {logger} from "firebase-functions/v2";
import {UserActionRequest} from "../model/request/UserActionRequest";
import {UserStatusType} from "../model/UserStatus";
import {throwFunctionsHttpsError} from "../util/functions_helper";
import CloudTasksUtil from "../util/CloudTasksUtil";
import UserStatusHandler from "../handler/UserStatusHandler";
import SeatStatusHandler from "../handler/SeatStatusHandler";
import {TimeoutRequest} from "../model/request/TimeoutRequest";

export function reserveSeatHandler(request: UserActionRequest): Promise<boolean> {
    logger.info("================= Reserve Seat ==================", {request: request});

    // Validate request
    if (request.targetStatusType !== UserStatusType.Reserved) {
        throwFunctionsHttpsError("invalid-argument", `Wrong target status type : ${request.targetStatusType}`);
    }

    if (request.deadlineInfo === undefined) {
        throwFunctionsHttpsError("invalid-argument", "Deadline info is undefined");
    }

    const promises: Promise<boolean>[] = [];
    const timer = new CloudTasksUtil();
    const keepStatusUntil = request.deadlineInfo?.keepStatusUntil;

    // 1. Handle seat status change
    promises.push(SeatStatusHandler.reserveSeat(
        request.userId,
        request.seatPosition,
    ));

    // 2. Start timer and handle user status change
    promises.push(timer.reserveUserSeatUpdate(
        TimeoutRequest.newInstance(
            request.userId,
            keepStatusUntil,
            UserStatusType.None,
            request.seatPosition,
            undefined,
            undefined,
        ),
        "timeoutOnReserve",
    ).then((task) => {
        if (!task.name) {
            throwFunctionsHttpsError("internal", "Timer task failed to start");
        }
        return UserStatusHandler.reserveSeat(
            request.userId,
            request.seatPosition,
            request.startStatusAt,
            keepStatusUntil,
            task.name
        );
    }));

    // 3. TODO: handle user history update

    return Promise.all(promises).then((results) => {
        return results.every((result) => result);
    });
}
