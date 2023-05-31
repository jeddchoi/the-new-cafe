import {UserActionRequest} from "../model/request/UserActionRequest";
import {logger} from "firebase-functions/v2";
import {UserStatusType} from "../model/UserStatus";
import {throwFunctionsHttpsError} from "../util/functions_helper";
import SeatStatusHandler from "../handler/SeatStatusHandler";
import UserStatusHandler from "../handler/UserStatusHandler";
import RealtimeDatabaseUtil from "../util/RealtimeDatabaseUtil";
import CloudTasksUtil from "../util/CloudTasksUtil";

export function cancelReservationHandler(request: UserActionRequest): Promise<boolean> {
    logger.info("================= Cancel Reservation ==================", {request: request});

    // Validate request
    if (request.targetStatusType !== UserStatusType.None) {
        throwFunctionsHttpsError("invalid-argument", `Wrong target status type : ${request.targetStatusType}`);
    }


    const promises: Promise<boolean>[] = [];
    const timer = new CloudTasksUtil();


    // 1. Stop timer and handle user status change
    promises.push(RealtimeDatabaseUtil.getUserStatusData(request.userId).then((userStatus) => {
        if (!userStatus.currentTimer) {
            return true;
        }
        return timer.cancelTimer(userStatus.currentTimer?.timerTaskName);
    }).then(() => {
        return UserStatusHandler.cancelReservation(
            request.userId,
            request.seatPosition,
            request.startStatusAt,
            request.reason
        );
    }));

    // 2. Handle seat status change
    promises.push(SeatStatusHandler.cancelReservation(
        request.userId,
        request.seatPosition,
    ));

    // 3. TODO: handle user history update

    return Promise.all(promises).then((results) => {
        return results.every((result) => result);
    });
}
