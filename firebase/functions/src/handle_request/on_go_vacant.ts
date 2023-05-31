import {UserActionRequest} from "../model/request/UserActionRequest";
import {logger} from "firebase-functions/v2";
import {UserStatusType} from "../model/UserStatus";
import {throwFunctionsHttpsError} from "../util/functions_helper";
import CloudTasksUtil from "../util/CloudTasksUtil";
import SeatStatusHandler from "../handler/SeatStatusHandler";
import UserStatusHandler from "../handler/UserStatusHandler";
import {TimeoutRequest} from "../model/request/TimeoutRequest";


export function goVacantHandler(request: UserActionRequest): Promise<boolean> {
    logger.info("================= Go Vacant ==================", {request: request});

    // Validate request
    if (request.targetStatusType !== UserStatusType.Vacant) {
        throwFunctionsHttpsError("invalid-argument", `Wrong target status type : ${request.targetStatusType}`);
    }
    if (request.deadlineInfo === undefined) {
        throwFunctionsHttpsError("invalid-argument", "Deadline info is undefined");
    }


    const promises: Promise<boolean>[] = [];
    const timer = new CloudTasksUtil();
    const keepStatusUntil = request.deadlineInfo?.keepStatusUntil;

    // 1. Handle seat status change
    promises.push(SeatStatusHandler.leaveSeat(
        request.userId,
        request.seatPosition,
    ));

    // 2. Start timer and handle user status change
    promises.push(timer.startTimer(
        TimeoutRequest.newInstance(
            request.userId,
            keepStatusUntil,
            UserStatusType.None,
            request.seatPosition,
            "timeoutOnVacant",
            undefined,
            undefined,
        ),
    ).then((task) => {
        if (!task.name) {
            throwFunctionsHttpsError("internal", "Timer task failed to start");
        }
        return UserStatusHandler.goVacant(
            request.userId,
            request.seatPosition,
            request.startStatusAt,
            keepStatusUntil,
            task.name,
            request.reason,
        );
    }));

    // 3. TODO: handle user history update

    return Promise.all(promises).then((results) => {
        return results.every((result) => result);
    });
}
