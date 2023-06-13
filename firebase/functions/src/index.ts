// noinspection JSUnusedGlobalSymbols

import {initializeApp} from "firebase-admin/app";

initializeApp();

import {CallableRequest, onCall, onRequest, Request} from "firebase-functions/v2/https";
import {onDocumentWritten} from "firebase-functions/v2/firestore";
import {onValueWritten} from "firebase-functions/v2/database";
import {logger} from "firebase-functions/v2";
import {auth} from "firebase-functions";
import {Response} from "express";

import {UserActionRequest} from "./model/UserActionRequest";
import {requestHandler} from "./handle_request/on_handle_request";
import {
    COLLECTION_GROUP_SEAT_NAME,
    COLLECTION_GROUP_SECTION_NAME,
    COLLECTION_GROUP_STORE_NAME,
} from "./model/SeatPosition";

import {throwFunctionsHttpsError} from "./util/functions_helper";
import {REFERENCE_USER_STATE_NAME} from "./util/RealtimeDatabaseUtil";
import {seatWrittenHandler} from "./trigger/on_seat_written";
import {sectionWrittenHandler} from "./trigger/on_section_written";
import {timerWrittenHandler} from "./trigger/on_timer_written";
import {authUserCreatedHandler} from "./trigger/on_auth_user_created";
import {TimeoutRequest} from "./util/CloudTasksUtil";
import {afterRequestHandler} from "./handle_request/after_handle_request";
import {UserStateChangeReason} from "./model/UserStateChangeReason";


/**
 * Callable functions
 */
export const onHandleRequest =
    onCall<UserActionRequest>((
        request: CallableRequest<UserActionRequest>,
    ) => {
        if (request.auth === undefined) {
            throwFunctionsHttpsError("unauthenticated", "User is not authenticated");
        }
        const userId = request.auth.uid;
        const current = new Date().getTime();
        return requestHandler(userId, request.data.requestType, request.data.seatPosition, request.data.getEndTime(current), current)
            .then(() => {
                logger.info("Completed Successfully.");
                return afterRequestHandler(userId, request.data.requestType, true, UserStateChangeReason.UserAction, request.data.seatPosition, current);
            }).catch((e) => {
                logger.error("Some error occurred", e);
                return afterRequestHandler(userId, request.data.requestType, false, UserStateChangeReason.UserAction, request.data.seatPosition, current);
            });
    });

/**
 * HTTP functions
 */
export const onHandleRequestTest =
    onRequest(async (req: Request, res: Response) => {
        const userId = req.body.userId as string;
        const request = UserActionRequest.fromJSON(req.body.request);
        const current = new Date().getTime();

        return requestHandler(userId, request.requestType, request.seatPosition, request.getEndTime(current), current)
            .then(() => {
                return afterRequestHandler(userId, request.requestType, true, UserStateChangeReason.UserAction, request.seatPosition, current).then(() => {
                    logger.info("Completed Successfully.");
                    res.status(200).send("Completed Successfully.");
                });
            }).catch((e) => {
                logger.error("Some error occurred", e);
                return afterRequestHandler(userId, request.requestType, false, UserStateChangeReason.UserAction, request.seatPosition, current).then(() => {
                    res.status(500).send(`Some error occurred. ${e}`);
                });
            });
    });

export const onTimeout =
    onRequest(async (req: Request, res: Response) => {
        const timeoutRequest = req.body as TimeoutRequest;

        return requestHandler(timeoutRequest.userId, timeoutRequest.requestType, null, null)
            .then(() => {
                return afterRequestHandler(timeoutRequest.userId, timeoutRequest.requestType, true, UserStateChangeReason.Timeout, null).then(() => {
                    logger.info("Completed Successfully.");
                    res.status(200).send("Completed Successfully.");
                });
            }).catch((e) => {
                logger.error("Some error occurred", e);
                return afterRequestHandler(timeoutRequest.userId, timeoutRequest.requestType, false, UserStateChangeReason.Timeout, null).then(() => {
                    res.status(500).send(`Some error occurred. ${e}`);
                });
            });
    });

/**
 * Triggered functions
 */

/**
 * Update the section's totalAvailableSeats by counting the number of seats for which isAvailable = true
 */
export const onSeatWritten =
    onDocumentWritten(
        {
            region: "asia-northeast3",
            document: `${COLLECTION_GROUP_STORE_NAME}/{storeId}/${COLLECTION_GROUP_SECTION_NAME}/{sectionId}/${COLLECTION_GROUP_SEAT_NAME}/{seatId}`,
        },
        seatWrittenHandler);

/**
 * Update the store's totalAvailableSeats by adding the totalAvailableSeats per section
 */
export const onSectionWritten =
    onDocumentWritten(
        {
            region: "asia-northeast3",
            document: `${COLLECTION_GROUP_STORE_NAME}/{storeId}/${COLLECTION_GROUP_SECTION_NAME}/{sectionId}`,
        },
        sectionWrittenHandler
    );


/**
 * Schedule a Cloud task when created overall timer
 * If it is fired, requestHandler will handle it
 */
export const onOverallTimerWritten =
    onValueWritten(
        {
            ref: `/${REFERENCE_USER_STATE_NAME}/{userId}/status/overall/timer`,
            region: "asia-southeast1",
        },
        timerWrittenHandler,
    );

/**
 * Schedule a Cloud task when created temporary timer
 * If it is fired, requestHandler will handle it
 */
export const onTemporaryTimerWritten =
    onValueWritten(
        {
            ref: `/${REFERENCE_USER_STATE_NAME}/{userId}/status/temporary/timer`,
            region: "asia-southeast1",
        },
        timerWrittenHandler
    );

/**
 * Create user state when auth user is created
 */
export const onAuthUserCreated =
    auth.user().onCreate(
        authUserCreatedHandler
    );

