import {https, logger} from "firebase-functions/v2";
import {Response} from "express";
import {projectID} from "firebase-functions/params";
import {UserActionRequest} from "../model/request/UserActionRequest";
import {UserStatusType} from "../model/UserStatus";
import {throwFunctionsHttpsError} from "../util/functions_helper";
import SeatStatusHandler from "../handler/SeatStatusHandler";
import UserStatusHandler from "../handler/UserStatusHandler";
import {TimeoutRequest} from "../model/request/TimeoutRequest";


export const timeoutOnReserveHandler = (
    request: https.Request,
    response: Response
) => {
    const timeoutRequest = TimeoutRequest.fromPaylod(request.body);
    logger.info(`Timeout on Reservation : ${timeoutRequest.toString()}`);

    // Validate request
    if (timeoutRequest.targetStatusType !== UserStatusType.None) {
        throwFunctionsHttpsError("invalid-argument", `Wrong target status type : ${timeoutRequest.targetStatusType}`);
    }


    const promises = [];

    // 1. Handle user status change
    promises.push(UserStatusHandler.cancelReservation(
        timeoutRequest.userId,
        timeoutRequest.seatPosition,
        timeoutRequest.requestedAt,
        timeoutRequest.reason,
    ));

    // 2. Handle seat status change
    promises.push(SeatStatusHandler.cancelReservation(
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

