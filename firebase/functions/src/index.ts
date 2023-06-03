// noinspection JSUnusedGlobalSymbols

import {initializeApp} from "firebase-admin/app";
initializeApp();

import {CallableRequest, onCall, onRequest, Request} from "firebase-functions/v2/https";
import {onDocumentWritten} from "firebase-functions/v2/firestore";
import {Response} from "express";

import {MyRequest} from "./model/MyRequest";
import {requestHandler} from "./handle_request/on_handle_request";
import {throwFunctionsHttpsError} from "./util/functions_helper";

import {
    COLLECTION_GROUP_SEAT_NAME,
    COLLECTION_GROUP_SECTION_NAME,
    COLLECTION_GROUP_STORE_NAME,
} from "./util/FirestoreUtil";
import {countSeatChangeHandler} from "./trigger/count_seat_change";
import {countSectionChangeHandler} from "./trigger/count_section_change";
import {logger} from "firebase-functions/v2";


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

export const onTimeout =
    onRequest(async (req: Request, res: Response) => {
        try {
            await requestHandler(req.body as MyRequest, true);
            logger.info("Completed Successfully.");
            res.status(200).send("Completed Successfully.");
        } catch (e) {
            logger.error("Some error occurred", e);
            res.status(500).send(`Some error occurred. ${e}`);
        }
    });

export const test =
    onRequest(async (req: Request, res: Response) => {
        try {
            await requestHandler(req.body as MyRequest, false);
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
export const countSeatChange =
    onDocumentWritten(
        {
            document: `${COLLECTION_GROUP_STORE_NAME}/{storeId}/${COLLECTION_GROUP_SECTION_NAME}/{sectionId}/${COLLECTION_GROUP_SEAT_NAME}/{seatId}`,
        },
        countSeatChangeHandler);

export const countSectionChange =
    onDocumentWritten(
        {
            document: `${COLLECTION_GROUP_STORE_NAME}/{storeId}/${COLLECTION_GROUP_SECTION_NAME}/{sectionId}`,
        },
        countSectionChangeHandler
    );
