import {https, logger} from "firebase-functions/v2";
import {Response} from "express";
import {projectID} from "firebase-functions/params";
import {UserSeatUpdateRequest} from "../model/UserSeatUpdateRequest";
import {UserStatusType} from "../model/UserStatus";
import {throwFunctionsHttpsError} from "../util/functions_helper";
import SeatStatusHandler from "../handler/SeatStatusHandler";
import UserStatusHandler from "../handler/UserStatusHandler";


export const timeoutOnReserveHandler = (
    request: https.Request,
    response: Response
) => {
    const userSeatUpdateRequest = request.body as UserSeatUpdateRequest;
    logger.info(`cancel reservation : ${new Date().toISOString()}`, {structuredData: JSON.stringify(userSeatUpdateRequest)},);

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
    promises.push(UserStatusHandler.cancelReservation(
        // request.auth?.uid,
        "sI2wbdRqYtdgArsq678BFSGDwr43",
        userSeatUpdateRequest.seatPosition,
        userSeatUpdateRequest.until,
        userSeatUpdateRequest.reason,
    ));

    // 2. Handle seat status change
    promises.push(SeatStatusHandler.cancelReservation(
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

