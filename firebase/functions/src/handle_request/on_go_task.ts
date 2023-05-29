import {UserActionRequest} from "../model/request/UserActionRequest";
import {logger} from "firebase-functions/v2";
import {UserStatusType} from "../model/UserStatus";
import {throwFunctionsHttpsError} from "../util/functions_helper";
import CloudTasksUtil from "../util/CloudTasksUtil";
import SeatStatusHandler from "../handler/SeatStatusHandler";
import UserStatusHandler from "../handler/UserStatusHandler";
import {TimeoutRequest} from "../model/request/TimeoutRequest";


export function goTaskHandler(request: UserActionRequest): Promise<boolean> {
    logger.info("================= Go Task ==================", {request: request});

    // Validate request
    if (request.targetStatusType !== UserStatusType.OnTask) {
        throwFunctionsHttpsError("invalid-argument", `Wrong target status type : ${request.targetStatusType}`);
    }

    const promises: Promise<boolean>[] = [];

    const timer = new CloudTasksUtil();

    // 1. Handle seat status change
    promises.push(SeatStatusHandler.leaveSeat(
        request.userId,
        request.seatPosition,
    ));

    // 2. Start timer and handle user status change
    promises.push(timer.reserveUserSeatUpdate(
        new TimeoutRequest(
            request.seatPosition,
            request.userId,
            UserStatusType.Vacant,
            100,
        ),
        "/timeoutOnTask",
    ).then((task) => {
        if (!task.name) {
            throwFunctionsHttpsError("internal", "Timer task failed to start");
        }
        return UserStatusHandler.onTask(
            request.userId,
            request.seatPosition,
            request.requestedAt,
            request.until,
            task.name,
        );
    }));

    // 3. TODO: handle user history update

    return Promise.all(promises).then((results) => {
        return results.every((result) => result);
    });
}
