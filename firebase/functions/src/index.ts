import {initializeApp} from "firebase-admin/app";
initializeApp();

import {CallableRequest, onCall, onRequest, Request} from "firebase-functions/v2/https";
import {onDocumentWritten} from "firebase-functions/v2/firestore";
import {Response} from "express";

import {MyRequest} from "./model/MyRequest";
import {RequestType} from "./model/RequestType";
import {ISeatPosition} from "./model/UserStatus";
import {UserStatusChangeReason} from "./model/UserStatusChangeReason";
import {requestHandler} from "./handle_request/on_handle_request";
import {throwFunctionsHttpsError} from "./util/functions_helper";

import {
    COLLECTION_GROUP_SEAT_NAME,
    COLLECTION_GROUP_SECTION_NAME,
    COLLECTION_GROUP_STORE_NAME,
} from "./util/FirestoreUtil";
import {countSeatChangeHandler} from "./firestore/count_seat_change";
import {countSectionChangeHandler} from "./firestore/count_section_change";

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
        return requestHandler(request.data);
    });

/**
 * HTTP functions
 */

export const onTimeout =
    onRequest(async (req: Request, res: Response) => {
        try {
            await requestHandler(req.body as MyRequest);
            res.status(200).send("Completed Successfully.");
        } catch (e) {
            res.status(500).send(`Some error occurred. ${e}`);
        }
    });


export const testReserveSeat = onRequest(async (req: Request, res: Response) => {
    try {
        await requestHandler(MyRequest.newInstance(
            RequestType.ReserveSeat,
            "sI2wbdRqYtdgArsq678BFSGDwr43",
            UserStatusChangeReason.UserAction,
            undefined,
            <ISeatPosition>{
                "storeId": "i9sAij5mVBijR85hgraE",
                "sectionId": "FMLYWLzKmiou1PTcrFR8",
                "seatId": "ZlblGsMYd7IlO1DEho4H",
            },
            50,
        ));
        res.status(200).send("Completed Successfully.");
    } catch (e) {
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
