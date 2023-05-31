import {UserActionRequest} from "../model/request/UserActionRequest";
import {logger} from "firebase-functions/v2";
import {UserStatusType} from "../model/UserStatus";
import {throwFunctionsHttpsError} from "../util/functions_helper";
import CloudTasksUtil from "../util/CloudTasksUtil";
import RealtimeDatabaseUtil from "../util/RealtimeDatabaseUtil";
import UserStatusHandler from "../handler/UserStatusHandler";
import SeatStatusHandler from "../handler/SeatStatusHandler";


export function stopUsingSeatHandler(request: UserActionRequest): Promise<boolean> {
    logger.info("================= Stop Using Seat ==================", {request: request});

    // Validate request
    if (request.targetStatusType !== UserStatusType.None) {
        throwFunctionsHttpsError("invalid-argument", `Wrong target status type : ${request.targetStatusType}`);
    }


    const promises: Promise<boolean>[] = [];
    const timer = new CloudTasksUtil();


    // 1. Stop timer and handle user status change
    promises.push(RealtimeDatabaseUtil.getUserStatusData(request.userId).then((userStatus) => {
        if (!userStatus.currentTimer && !userStatus.usageTimer) {
            return true;
        }
        const proms = [];
        if (userStatus.currentTimer) {
            proms.push(timer.cancelTimer(userStatus.currentTimer?.timerTaskName));
        }
        if (userStatus.usageTimer) {
            proms.push(timer.cancelTimer(userStatus.usageTimer?.timerTaskName));
        }
        return Promise.all(proms).then((results) => {
            return results.every((result) => result);
        });
    }).then((result) => {
        if (result) {
            logger.debug("all timers stopped successfully");
        }
        return UserStatusHandler.stopUsingSeat(
            request.userId,
            request.seatPosition,
            request.startStatusAt,
            request.reason
        );
    }));

    // 2. Handle seat status change
    promises.push(SeatStatusHandler.stopUsingSeat(
        request.userId,
        request.seatPosition,
    ));

    // 3. TODO: handle user history update

    return Promise.all(promises).then((results) => {
        return results.every((result) => result);
    });
}
