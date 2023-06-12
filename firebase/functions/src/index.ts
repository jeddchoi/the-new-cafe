// noinspection JSUnusedGlobalSymbols

import {initializeApp} from "firebase-admin/app";
import {CallableRequest, onCall, onRequest, Request} from "firebase-functions/v2/https";
import {onDocumentWritten} from "firebase-functions/v2/firestore";
import {onValueWritten} from "firebase-functions/v2/database";
import {logger} from "firebase-functions/v2";
import {Response} from "express";
import {auth} from "firebase-functions";

import {UserActionRequest} from "./model/UserActionRequest";
import {requestHandler} from "./handle_request/on_handle_request";
import {throwFunctionsHttpsError} from "./util/functions_helper";

import {seatWrittenHandler} from "./trigger/on_seat_written";
import {sectionWrittenHandler} from "./trigger/on_section_written";
import {
    COLLECTION_GROUP_SEAT_NAME,
    COLLECTION_GROUP_SECTION_NAME,
    COLLECTION_GROUP_STORE_NAME,
} from "./model/SeatPosition";
import RealtimeDatabaseUtil, {REFERENCE_USER_STATE_NAME} from "./util/RealtimeDatabaseUtil";
import {overallTimerWrittenHandler} from "./trigger/on_overall_timer_written";
import {temporaryTimerWrittenHandler} from "./trigger/on_temporary_timer_written";
import {userStateStatusWrittenHandler} from "./trigger/on_user_state_status_written";
import {IUserStateExternal} from "./model/UserState";

initializeApp();


/**
 * Callable functions
 */
export const onHandleRequest =
    onCall<UserActionRequest, Promise<void>>((
        request: CallableRequest<UserActionRequest>,
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
export const onSeatWritten =
    onDocumentWritten(
        {
            document: `${COLLECTION_GROUP_STORE_NAME}/{storeId}/${COLLECTION_GROUP_SECTION_NAME}/{sectionId}/${COLLECTION_GROUP_SEAT_NAME}/{seatId}`,
        },
        seatWrittenHandler);

/**
 * Update the store's totalAvailableSeats by adding the totalAvailableSeats per section
 */
export const onSectionWritten =
    onDocumentWritten(
        {
            document: `${COLLECTION_GROUP_STORE_NAME}/{storeId}/${COLLECTION_GROUP_SECTION_NAME}/{sectionId}`,
        },
        sectionWrittenHandler
    );


/**
 * Schedule a Cloud task when there is data in UserState's status/overall/timer
 * If it is fired, delete status of UserState
 */
export const onOverallTimerWritten =
    onValueWritten(
        `/${REFERENCE_USER_STATE_NAME}/{userId}/status/overall/timer`,
        overallTimerWrittenHandler
    );

/**
 * Schedule a Cloud task when there is data in UserState's status/temporary/timer
 * If it is fired, delete status of UserState when isReset = true
 * or delete temporary state of UserState when isReset = false
 */
export const onTemporaryTimerWritten =
    onValueWritten(
        `/${REFERENCE_USER_STATE_NAME}/{userId}/status/temporary/timer`,
        temporaryTimerWrittenHandler
    );

/**
 * Add state change to UserSession when status is written
 */
export const onUserStateStatusWritten =
    onValueWritten(
        `/${REFERENCE_USER_STATE_NAME}/{userId}/status`,
        userStateStatusWrittenHandler
    );

/**
 * Create user state when auth user is created
 */
export const onAuthUserCreated =
    auth.user().onCreate(
        (user) => {
            logger.info(`User ${user.uid} created.`);
            return RealtimeDatabaseUtil.getUserState(user.uid).set(<IUserStateExternal>{
                name: user.displayName,
                isOnline: false,
                status: null,
            });
        }
    );

