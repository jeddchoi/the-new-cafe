// noinspection JSUnusedGlobalSymbols

import {initializeApp} from "firebase-admin/app";
import {CallableRequest, onCall, onRequest, Request} from "firebase-functions/v2/https";
import {onDocumentWritten} from "firebase-functions/v2/firestore";
import {onValueWritten} from "firebase-functions/v2/database";
import {logger} from "firebase-functions/v2";
import {Response} from "express";
import {auth} from "firebase-functions";

import {MyRequest} from "./model/MyRequest";
import {requestHandler} from "./handle_request/on_handle_request";
import {throwFunctionsHttpsError} from "./util/functions_helper";

import {countSeatChangeHandler} from "./trigger/count_seat_change";
import {countSectionChangeHandler} from "./trigger/count_section_change";
import {COLLECTION_GROUP_SEAT_NAME, COLLECTION_GROUP_SECTION_NAME, COLLECTION_GROUP_STORE_NAME} from "./model/SeatId";
import RealtimeDatabaseUtil, {REFERENCE_USER_STATE_NAME} from "./util/RealtimeDatabaseUtil";
import {writeOverallTimerHandler} from "./trigger/write_overall_timer";
import {writeTemporaryTimerHandler} from "./trigger/write_temporary_timer";
import {writeUserStateStatusHandler} from "./trigger/write_user_state_status";
import {IUserStateExternal} from "./model/UserState";

initializeApp();

/**
 * Callable functions
 */
export const onHandleRequest =
    onCall<MyRequest, Promise<void>>((
        request: CallableRequest<MyRequest>,
    ): Promise<void> => {
        if (request.auth === undefined) {
            throwFunctionsHttpsError("unauthenticated", "User is not authenticated");
        }
        return requestHandler(request.data, false);
    });

/**
 * HTTP functions
 */

export const onDeletePathTimeout =
    onRequest(async (req: Request, res: Response) => {
        try {
            const deletePath = req.body.deletePath;
            await RealtimeDatabaseUtil.deletePath(deletePath);
            logger.info("Completed Successfully.");
            res.status(200).send("Completed Successfully.");
        } catch (e) {
            logger.error("Some error occurred", e);
            res.status(500).send(`Some error occurred. ${e}`);
        }
    });

/**
 * Triggered functions
 */

/**
 * Update the section's totalAvailableSeats by counting the number of seats for which isAvailable = true
 */
export const countSeatChange =
    onDocumentWritten(
        {
            document: `${COLLECTION_GROUP_STORE_NAME}/{storeId}/${COLLECTION_GROUP_SECTION_NAME}/{sectionId}/${COLLECTION_GROUP_SEAT_NAME}/{seatId}`,
        },
        countSeatChangeHandler);

/**
 * Update the store's totalAvailableSeats by adding the totalAvailableSeats per section
 */
export const countSectionChange =
    onDocumentWritten(
        {
            document: `${COLLECTION_GROUP_STORE_NAME}/{storeId}/${COLLECTION_GROUP_SECTION_NAME}/{sectionId}`,
        },
        countSectionChangeHandler
    );

/**
 * Schedule a Cloud task when there is data in UserState's status/overall/timer
 * If it is fired, delete status of UserState
 */
export const writeOverallTimer =
    onValueWritten(
        `/${REFERENCE_USER_STATE_NAME}/{userId}/status/overall/timer`,
        writeOverallTimerHandler
    );

/**
 * Schedule a Cloud task when there is data in UserState's status/temporary/timer
 * If it is fired, delete status of UserState when isReset = true
 * or delete temporary state of UserState when isReset = false
 */
export const writeTemporaryTimer =
    onValueWritten(
        `/${REFERENCE_USER_STATE_NAME}/{userId}/status/temporary/timer`,
        writeTemporaryTimerHandler
    );

/**
 * Add state change to UserSession when status is written
 */
export const writeUserStateStatus =
    onValueWritten(
        `/${REFERENCE_USER_STATE_NAME}/{userId}/status`,
        writeUserStateStatusHandler
    );


export const createUserStateOnUserCreate =
    auth.user().onCreate(
        (user, context) => {
            logger.info(`User ${user.uid} created.`);
            return RealtimeDatabaseUtil.getUserState(user.uid).set(<IUserStateExternal>{
                name: user.displayName,
                isOnline: false,
                status: null,
            });
        }
    );

