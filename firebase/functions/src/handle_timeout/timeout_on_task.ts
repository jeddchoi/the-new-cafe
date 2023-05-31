import {https, logger} from "firebase-functions/v2";
import {Response} from "express";
import {TimeoutRequest} from "../model/request/TimeoutRequest";
import {UserStatusChangeReason, UserStatusType} from "../model/UserStatus";
import {throwFunctionsHttpsError} from "../util/functions_helper";
import CloudTasksUtil from "../util/CloudTasksUtil";
import UserStatusHandler from "../handler/UserStatusHandler";
import SeatStatusHandler from "../handler/SeatStatusHandler";
import {projectID} from "firebase-functions/params";


export const timeoutOnTaskHandler = (
    request: https.Request,
    response: Response
) => {
    const timeoutRequest = TimeoutRequest.fromPayload(request.body);
    logger.info(`Timeout on Task : ${timeoutRequest.toString()}`);

    // Validate request
    if (timeoutRequest.targetStatusType !== UserStatusType.Vacant) {
        throwFunctionsHttpsError("invalid-argument", `Wrong target status type : ${timeoutRequest.targetStatusType}`);
    }
    if (timeoutRequest.deadlineInfo === undefined) {
        throwFunctionsHttpsError("invalid-argument", "Deadline info is undefined");
    }


    const promises = [];
    const timer = new CloudTasksUtil();
    const keepStatusUntil = timeoutRequest.deadlineInfo?.keepStatusUntil;

    // 1. Handle seat status change
    promises.push(SeatStatusHandler.leaveSeat(
        timeoutRequest.userId,
        timeoutRequest.seatPosition,
    ));

    // 2. Start timer and handle user status change
    promises.push(timer.startTimer(
        TimeoutRequest.newInstance(
            timeoutRequest.userId,
            keepStatusUntil,
            UserStatusType.None,
            timeoutRequest.seatPosition,
            undefined,
            undefined,
        ),
        "timeoutOnVacant",
    ).then((task) => {
        if (!task.name) {
            throwFunctionsHttpsError("internal", "Timer task failed to start");
        }
        return UserStatusHandler.goVacant(
            timeoutRequest.userId,
            timeoutRequest.seatPosition,
            timeoutRequest.startStatusAt,
            keepStatusUntil,
            task.name,
            UserStatusChangeReason.Timeout,
        );
    }));

    // 3. TODO: Handle user history update

    return Promise.all(promises).then((results) => {
        if (results.every((result) => result)) {
            response.send(`${projectID.value()}`);
        } else {
            response.sendStatus(400);
        }
    });
};
