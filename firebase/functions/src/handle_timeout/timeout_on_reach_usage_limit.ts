import {https, logger} from "firebase-functions/v2";
import {Response} from "express";
import {UserActionRequest} from "../model/request/UserActionRequest";
import {UserStatusType} from "../model/UserStatus";
import {throwFunctionsHttpsError} from "../util/functions_helper";
import UserStatusHandler from "../handler/UserStatusHandler";
import SeatStatusHandler from "../handler/SeatStatusHandler";
import {projectID} from "firebase-functions/params";
import {TimeoutRequest} from "../model/request/TimeoutRequest";
import RealtimeDatabaseUtil from "../util/RealtimeDatabaseUtil";
import CloudTasksUtil from "../util/CloudTasksUtil";


export const timeoutOnReachUsageLimitHandler = (
    request: https.Request,
    response: Response
) => {
    const timeoutRequest = TimeoutRequest.fromPaylod(request.body);
    logger.info(`Reached Usage Limit : ${timeoutRequest.toString()}`);

    // Validate request
    if (timeoutRequest.targetStatusType !== UserStatusType.None) {
        throwFunctionsHttpsError("invalid-argument", `Wrong target status type : ${timeoutRequest.targetStatusType}`);
    }

    const promises = [];
    const timer = new CloudTasksUtil();

    // 1. Stop current status timer if any, and handle user status change
    promises.push(
        RealtimeDatabaseUtil.getUserStatusData(timeoutRequest.userId).then((userStatus) => {
            if (!userStatus.currentTimer) {
                return true;
            }
            return timer.cancelTimer(userStatus.currentTimer?.timerTaskName);
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
