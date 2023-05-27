import {https, logger} from "firebase-functions/lib/v2";
import {Response} from "express";
import {UserSeatUpdateRequest} from "../model/UserSeatUpdateRequest";
import {UserStatusType} from "../model/UserStatus";
import {throwFunctionsHttpsError} from "../util/functions_helper";
import UserStatusHandler from "../handler/UserStatusHandler";
import SeatStatusHandler from "../handler/SeatStatusHandler";
import {projectID} from "firebase-functions/lib/params";


export const timeoutOnReachUsageLimitHandler = (
    request: https.Request,
    response: Response
) => {
    const userSeatUpdateRequest = request.body as UserSeatUpdateRequest;
    logger.info(`Reached Usage Limit : ${new Date().toISOString()}`, {structuredData: JSON.stringify(userSeatUpdateRequest)},);

    // Validate request
    if (userSeatUpdateRequest.targetStatusType !== UserStatusType.None) {
        throwFunctionsHttpsError("invalid-argument", `Wrong target status type : ${userSeatUpdateRequest.targetStatusType}`);
    }

    if (!userSeatUpdateRequest.seatPosition) {
        throwFunctionsHttpsError("invalid-argument", "Seat position is not provided");
    }

    if (!userSeatUpdateRequest.until) {
        throwFunctionsHttpsError("invalid-argument", "Until is not provided");
    }

    const promises = [];

    // 1. Handle user status change
    promises.push(UserStatusHandler.stopUsingSeat(
        // request.auth?.uid,
        "sI2wbdRqYtdgArsq678BFSGDwr43",
        userSeatUpdateRequest.seatPosition,
        userSeatUpdateRequest.until,
        userSeatUpdateRequest.reason,
    ));

    // 2. Handle seat status change
    promises.push(SeatStatusHandler.stopUsingSeat(
        // request.auth?.uid,
        "sI2wbdRqYtdgArsq678BFSGDwr43",
        userSeatUpdateRequest.seatPosition,
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
