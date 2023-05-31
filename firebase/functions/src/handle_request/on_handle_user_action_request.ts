import {UserActionRequest} from "../model/request/UserActionRequest";
import {logger} from "firebase-functions/v2";
import {IUserStatusExternal, UserStatusType} from "../model/UserStatus";
import {throwFunctionsHttpsError} from "../util/functions_helper";
import CloudTasksUtil from "../util/CloudTasksUtil";
import SeatStatusHandler from "../handler/SeatStatusHandler";
import {TimeoutRequest} from "../model/request/TimeoutRequest";
import UserStatusHandler from "../handler/UserStatusHandler";
import RealtimeDatabaseUtil from "../util/RealtimeDatabaseUtil";


export function onHandleUserActionRequest(
    request: UserActionRequest,
    predicateToRequest: ((userActionRequest: UserActionRequest) => boolean),
    predicateToExistingUserStatus: ((existingStatus: IUserStatusExternal) => boolean),
    stopCurrentTimer: boolean,
    stopUsageTimer: boolean,
    startCurrentTimer: TimeoutRequest | undefined | null,
    startUsageTimer: TimeoutRequest | undefined | null,
    updateUserStatus: boolean,
    updateSeatStatus: boolean,
): Promise<boolean> {
    logger.info("================= On Handle Request ==================", {request: request});

    // Validate request
    if (predicateToRequest(request)) {
        throwFunctionsHttpsError("invalid-argument", `Something is wrong : ${request.toString()}`);
    }

    const timer = new CloudTasksUtil();
    const promises: Promise<boolean>[] = [];


    promises.push(RealtimeDatabaseUtil.getUserStatusData(request.userId).then((userStatus) => {
        const stops = [];
        if (stopCurrentTimer && userStatus.currentTimer) {
            stops.push(timer.cancelTimer(userStatus.currentTimer.timerTaskName));
        }
        if (stopUsageTimer && userStatus.usageTimer) {
            stops.push(timer.cancelTimer(userStatus.usageTimer.timerTaskName));
        }
        return Promise.all(stops).then((results) => results.every((result) => result));
    }).then(async (result) => {
        if (!result) {
            throwFunctionsHttpsError("internal", "Stopping timer failed.");
        }
        if (startCurrentTimer) {
            return await timer.startTimer(startCurrentTimer);
        }
        if (startUsageTimer) {
            return await timer.startTimer(startUsageTimer);
        }
        return; // no need timer
    }).then(async (timerTask) => {
        if (updateUserStatus) {
            return await UserStatusHandler.updateUserStatus(request,
                predicateToExistingUserStatus,
                request.targetStatusType === UserStatusType.None,
                startCurrentTimer ? timerTask?.name : startCurrentTimer,
                startUsageTimer ? timerTask?.name : startUsageTimer,
            );
        }
        return true;
    }));

    promises.push()

    const keepStatusUntil = request.deadlineInfo?.keepStatusUntil;

    // 1. Handle seat status change
    promises.push(SeatStatusHandler.reserveSeat(
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
            "timeoutOnReserve",
            undefined,
            undefined,
        ),
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
