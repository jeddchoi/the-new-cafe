import {https, logger} from "firebase-functions/v2";
import {Response} from "express";
import {projectID} from "firebase-functions/params";
import {UserStatusType} from "../model/UserStatus";
import {TimeoutRequest} from "../model/request/TimeoutRequest";
import UserStatusHandler from "../handler/UserStatusHandler";
import SeatStatusHandler from "../handler/SeatStatusHandler";
import CloudTasksUtil from "../util/CloudTasksUtil";
import {throwFunctionsHttpsError} from "../util/functions_helper";
import RealtimeDatabaseUtil from "../util/RealtimeDatabaseUtil";

export const timeoutOnVacantHandler = (
    request: https.Request,
    response: Response
) => {
    const timeoutRequest = TimeoutRequest.fromPaylod(request.body);
    logger.info(`Timeout on Vacant : ${timeoutRequest.toString()}`);

    // Validate request
    if (timeoutRequest.targetStatusType !== UserStatusType.None) {
        throwFunctionsHttpsError("invalid-argument", `Wrong target status type : ${timeoutRequest.targetStatusType}`);
    }

    const promises = [];
    const timer = new CloudTasksUtil();

    // 1. Stop usage timer and handle user status change
    promises.push(RealtimeDatabaseUtil.getUserStatusData(timeoutRequest.userId).then((userStatus) => {
        if (!userStatus.usageTimer) {
            return true;
        }
        return timer.cancelTimer(userStatus.usageTimer?.timerTaskName);
    }).then(() => {
        return UserStatusHandler.stopUsingSeat(
            timeoutRequest.userId,
            timeoutRequest.seatPosition,
            timeoutRequest.requestedAt,
            timeoutRequest.reason,
        );
    }));

    // 2. Handle seat status change
    promises.push(SeatStatusHandler.stopUsingSeat(
        timeoutRequest.userId,
        timeoutRequest.seatPosition,
    ));

    // 3. TODO: Handle user history update

    return Promise.all(promises).then((results) => {
        if (results.every((result) => result)) {
            response.send(`${projectID.value()}`);
        } else {
            response.sendStatus(400);
        }
    });
};
