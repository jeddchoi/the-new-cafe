import {initializeApp} from "firebase-admin/app";

initializeApp();

// const functionRegion = defineString("MY_FUNCTIONS_LOCATION");
// setGlobalOptions({region: functionRegion.value()});
import {onCall, onRequest} from "firebase-functions/v2/https";
import {onDocumentWritten} from "firebase-functions/v2/firestore";

import {helloWorldHandler} from "./on-request/hello_world";
import {onReserve, reserveSeatHandler} from "./on-call/on_reserve";
import {
    COLLECTION_GROUP_SEAT_NAME,
    COLLECTION_GROUP_SECTION_NAME,
    COLLECTION_GROUP_STORE_NAME,
} from "./util/FirestoreUtil";
import {countSeatChangeHandler} from "./firestore/count_seat_change";
import {countSectionChangeHandler} from "./firestore/count_section_change";
import {UserSeatUpdateRequest} from "./model/UserSeatUpdateRequest";
import {timeoutOnReserveHandler} from "./on-request/timeout_on_reserve";
import {cancelReservationHandler, onCancelReservation} from "./on-call/on_cancel_reservation";
import {UserStatusChangeReason, UserStatusType} from "./model/UserStatus";

// Callable functions
export const reserveSeat =
    onCall<UserSeatUpdateRequest, Promise<boolean>>(reserveSeatHandler);

export const cancelReservation =
    onCall<UserSeatUpdateRequest, Promise<boolean>>(cancelReservationHandler);


// HTTP functions
export const helloWorld =
    onRequest(helloWorldHandler);

export const timeoutOnReserve =
    onRequest(timeoutOnReserveHandler);


// Test functions
export const testReserveSeat = onRequest((req, res) => {
    return onReserve(
        new UserSeatUpdateRequest(
            "sI2wbdRqYtdgArsq678BFSGDwr43",
            UserStatusType.Reserved,
            UserStatusChangeReason.UserAction,
            {"storeId": "i9sAij5mVBijR85hgraE", "sectionId": "FMLYWLzKmiou1PTcrFR8", "seatId": "ZlblGsMYd7IlO1DEho4H"},
            100
        )
    ).then((result) => {
        if (result) {
            res.sendStatus(200);
        } else {
            res.sendStatus(500);
        }
    });
});


export const testCancelReservation = onRequest((req, res) => {
    return onCancelReservation(
        new UserSeatUpdateRequest(
            "sI2wbdRqYtdgArsq678BFSGDwr43",
            UserStatusType.None,
            UserStatusChangeReason.UserAction,
            {"storeId": "i9sAij5mVBijR85hgraE", "sectionId": "FMLYWLzKmiou1PTcrFR8", "seatId": "ZlblGsMYd7IlO1DEho4H"},
        )
    ).then((result) => {
        if (result) {
            res.sendStatus(200);
        } else {
            res.sendStatus(500);
        }
    });
});

// Triggered functions
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

