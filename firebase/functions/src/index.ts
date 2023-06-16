// noinspection JSUnusedGlobalSymbols

import "./util/firebase_initialize"; // This must be located on top
import {Response} from "express";
import {auth} from "firebase-functions";
import {logger} from "firebase-functions/v2";
import {onValueWritten} from "firebase-functions/v2/database";
import {onDocumentWritten} from "firebase-functions/v2/firestore";
import {CallableRequest, onCall, onRequest, Request} from "firebase-functions/v2/https";
import {afterRequestHandler} from "./handle_request/after_handle_request";
import {requestHandler} from "./handle_request/on_handle_request";
import {BusinessResultCode} from "./model/BusinessResultCode";
import {
    COLLECTION_GROUP_SEAT_NAME,
    COLLECTION_GROUP_SECTION_NAME,
    COLLECTION_GROUP_STORE_NAME,
} from "./model/SeatPosition";

import {UserActionRequest} from "./model/UserActionRequest";
import {UserStateChangeReason} from "./model/UserStateChangeReason";
import {authUserCreatedHandler} from "./trigger/on_auth_user_created";
import {seatWrittenHandler} from "./trigger/on_seat_written";
import {sectionWrittenHandler} from "./trigger/on_section_written";
import {timerWrittenHandler} from "./trigger/on_timer_written";
import {TimeoutRequest} from "./util/CloudTasksUtil";
import {isBusinessResultCode} from "./util/functions_helper";

import {REFERENCE_USER_STATE_NAME} from "./util/RealtimeDatabaseUtil";

/**
 * Callable functions
 */
export const onHandleRequest =
    onCall<UserActionRequest, Promise<BusinessResultCode>>(
        {
            timeoutSeconds: 30,
        },
        (
            request: CallableRequest<UserActionRequest>,
        ) => Promise.resolve().then(() => {
            if (!request.auth) {
                throw BusinessResultCode.UNAUTHENTICATED;
            }

            const userId = request.auth.uid;
            const current = new Date().getTime();

            return requestHandler(userId, request.data.requestType, request.data.seatPosition, request.data.getEndTime(current), current)
                .catch((requestError) => {
                    logger.error("On handling request, error occurred : ", requestError);
                    return afterRequestHandler(userId, request.data.requestType, false, UserStateChangeReason.UserAction, request.data.seatPosition, current)
                        .catch((afterRequestError) => {
                            logger.error("After request error also occurred", afterRequestError);
                            throw requestError;
                        })
                        .then(() => {
                            throw requestError; // even if error occurred after handling request, return request error
                        });
                }).then(() => {
                    logger.info("Handling request completed successfully.");
                    return afterRequestHandler(userId, request.data.requestType, true, UserStateChangeReason.UserAction, request.data.seatPosition, current);
                });
        }).then(() => {
            return BusinessResultCode.OK;
        }).catch((err) => {
            if (isBusinessResultCode((err))) { // business error
                return err;
            }
            throw err;
        }));


/**
 * HTTP functions
 */
export const onHandleRequestTest =
    onRequest((req: Request, res: Response) => Promise.resolve().then(() => {
        const userId = req.body.userId as string;
        const request = UserActionRequest.fromJSON(req.body.request);
        const current = new Date().getTime();

        return requestHandler(userId, request.requestType, request.seatPosition, request.getEndTime(current), current)
            .catch((requestError) => {
                logger.error("On handling request, error occurred : ", requestError);
                return afterRequestHandler(userId, request.requestType, false, UserStateChangeReason.UserAction, request.seatPosition, current)
                    .catch((afterRequestError) => {
                        logger.error("After request error also occurred", afterRequestError);
                        throw requestError;
                    })
                    .then(() => {
                        throw requestError; // even if error occurred after handling request, return request error
                    });
            }).then(() => {
                logger.info("Handling request completed successfully.");
                return afterRequestHandler(userId, request.requestType, true, UserStateChangeReason.UserAction, request.seatPosition, current);
            });
    }).then(() => {
        res.status(200).send(BusinessResultCode.OK);
    }).catch((err) => {
        if (isBusinessResultCode((err))) { // business error
            res.status(200).send(err);
            // return err;
        } else {
            res.status(500).send(err);
        }
    }));

export const onTimeout =
    onRequest(async (req: Request, res: Response) => Promise.resolve().then(() => {

        const request = req.body as TimeoutRequest;
        const userId = request.userId;
        return requestHandler(userId, request.requestType, null, null)
            .catch((requestError) => {
                logger.error("On handling request, error occurred : ", requestError);
                return afterRequestHandler(userId, request.requestType, false, UserStateChangeReason.Timeout, null)
                    .catch((afterRequestError) => {
                        logger.error("After request error also occurred", afterRequestError);
                        throw requestError;
                    })
                    .then(() => {
                        throw requestError; // even if error occurred after handling request, return request error
                    });
            }).then(() => {
                logger.info("Handling request completed successfully.");
                return afterRequestHandler(userId, request.requestType, true, UserStateChangeReason.Timeout, null);
            });
    }).then(() => {
        res.status(200).send(BusinessResultCode.OK);
    }).catch((err) => {
        if (isBusinessResultCode((err))) { // business error
            res.status(200).send(err);
        }
        res.status(500).send(err);
    }));


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

