import {initializeApp} from "firebase-admin/app";

initializeApp();

// const functionRegion = defineString("MY_FUNCTIONS_LOCATION");
// setGlobalOptions({region: functionRegion.value()});
import {onCall, onRequest, CallableRequest} from "firebase-functions/v2/https";
import {onDocumentWritten} from "firebase-functions/v2/firestore";

import {
    COLLECTION_GROUP_SEAT_NAME,
    COLLECTION_GROUP_SECTION_NAME,
    COLLECTION_GROUP_STORE_NAME,
} from "./util/FirestoreUtil";

import {UserStatusChangeReason, UserStatusType} from "./model/UserStatus";
import {UserSeatUpdateRequest} from "./model/UserSeatUpdateRequest";

/**
 * Callable functions
  */

import {reserveSeatHandler} from "./handle_request/on_reserve";
import {cancelReservationHandler} from "./handle_request/on_cancel_reservation";
import {occupySeatHandler} from "./handle_request/on_occupy_seat";
import {stopUsingSeatHandler} from "./handle_request/on_stop_using_seat";
import {goVacantHandler} from "./handle_request/on_go_vacant";
import {goTaskHandler} from "./handle_request/on_go_task";


export const reserveSeat =
    onCall<UserSeatUpdateRequest, Promise<boolean>>((
        request: CallableRequest<UserSeatUpdateRequest>,
    ): Promise<boolean> => reserveSeatHandler(request.data));

export const testReserveSeat = onRequest((req, res) => {
    return reserveSeatHandler(
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

export const cancelReservation =
    onCall<UserSeatUpdateRequest, Promise<boolean>>((
        request: CallableRequest<UserSeatUpdateRequest>,
    ): Promise<boolean> => cancelReservationHandler(request.data));

export const testCancelReservation = onRequest((req, res) => {
    return cancelReservationHandler(
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

export const occupySeat =
    onCall<UserSeatUpdateRequest, Promise<boolean>>((
        request: CallableRequest<UserSeatUpdateRequest>,
    ): Promise<boolean> => occupySeatHandler(request.data));

export const testOccupySeat = onRequest((req, res) => {
    return occupySeatHandler(
        new UserSeatUpdateRequest(
            "sI2wbdRqYtdgArsq678BFSGDwr43",
            UserStatusType.Occupied,
            UserStatusChangeReason.UserAction,
            {"storeId": "i9sAij5mVBijR85hgraE", "sectionId": "FMLYWLzKmiou1PTcrFR8", "seatId": "ZlblGsMYd7IlO1DEho4H"},
            1000,
        )
    ).then((result) => {
        if (result) {
            res.sendStatus(200);
        } else {
            res.sendStatus(500);
        }
    });
});

export const stopUsingSeat =
    onCall<UserSeatUpdateRequest, Promise<boolean>>((
        request: CallableRequest<UserSeatUpdateRequest>,
    ): Promise<boolean> => stopUsingSeatHandler(request.data));


export const testStopUsingSeat = onRequest((req, res) => {
    return stopUsingSeatHandler(
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

export const goVacant =
    onCall<UserSeatUpdateRequest, Promise<boolean>>((
        request: CallableRequest<UserSeatUpdateRequest>,
    ): Promise<boolean> => goVacantHandler(request.data));

export const testGoVacant = onRequest((req, res) => {
    return goVacantHandler(
        new UserSeatUpdateRequest(
            "sI2wbdRqYtdgArsq678BFSGDwr43",
            UserStatusType.Occupied,
            UserStatusChangeReason.UserAction,
            {"storeId": "i9sAij5mVBijR85hgraE", "sectionId": "FMLYWLzKmiou1PTcrFR8", "seatId": "ZlblGsMYd7IlO1DEho4H"},
            100,
        )
    ).then((result) => {
        if (result) {
            res.sendStatus(200);
        } else {
            res.sendStatus(500);
        }
    });
});

export const goTask =
    onCall<UserSeatUpdateRequest, Promise<boolean>>((
        request: CallableRequest<UserSeatUpdateRequest>,
    ): Promise<boolean> => goTaskHandler(request.data));


export const testGoTask = onRequest((req, res) => {
    return goTaskHandler(
        new UserSeatUpdateRequest(
            "sI2wbdRqYtdgArsq678BFSGDwr43",
            UserStatusType.OnTask,
            UserStatusChangeReason.UserAction,
            {"storeId": "i9sAij5mVBijR85hgraE", "sectionId": "FMLYWLzKmiou1PTcrFR8", "seatId": "ZlblGsMYd7IlO1DEho4H"},
            100,
        )
    ).then((result) => {
        if (result) {
            res.sendStatus(200);
        } else {
            res.sendStatus(500);
        }
    });
});

/**
 * HTTP functions
  */

import {helloWorldHandler} from "./handle_timeout/hello_world";
import {timeoutOnReserveHandler} from "./handle_timeout/timeout_on_reserve";
import {timeoutOnReachUsageLimitHandler} from "./handle_timeout/timeout_on_reach_usage_limit";
import {timeoutOnVacantHandler} from "./handle_timeout/timeout_on_vacant";

export const helloWorld =
    onRequest(helloWorldHandler);

export const timeoutOnReserve =
    onRequest(timeoutOnReserveHandler);

export const timeoutOnReachUsageLimit =
    onRequest(timeoutOnReachUsageLimitHandler);


export const timeoutOnVacant =
    onRequest(timeoutOnVacantHandler);


/**
 * Triggered functions
  */

import {countSeatChangeHandler} from "./firestore/count_seat_change";
import {countSectionChangeHandler} from "./firestore/count_section_change";

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

