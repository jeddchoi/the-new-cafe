import {initializeApp} from "firebase-admin/app";

initializeApp();

import {onCall, onRequest, CallableRequest} from "firebase-functions/v2/https";
import {onDocumentWritten} from "firebase-functions/v2/firestore";

import {
    COLLECTION_GROUP_SEAT_NAME,
    COLLECTION_GROUP_SECTION_NAME,
    COLLECTION_GROUP_STORE_NAME,
} from "./util/FirestoreUtil";

import {UserStatusType} from "./model/UserStatus";
import {UserActionRequest} from "./model/request/UserActionRequest";

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
    onCall<UserActionRequest, Promise<boolean>>((
        request: CallableRequest<UserActionRequest>,
    ): Promise<boolean> => {
        if (request.auth === undefined) {
            throwFunctionsHttpsError("unauthenticated", "User is not authenticated");
        }
        return reserveSeatHandler(request.data);
    });

export const testReserveSeat = onRequest((req, res) => {
    return reserveSeatHandler(
        UserActionRequest.newInstance(
            "sI2wbdRqYtdgArsq678BFSGDwr43",
            undefined,
            UserStatusType.Reserved,
            {"storeId": "i9sAij5mVBijR85hgraE", "sectionId": "FMLYWLzKmiou1PTcrFR8", "seatId": "ZlblGsMYd7IlO1DEho4H"},
            100,
            undefined,
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
    onCall<UserActionRequest, Promise<boolean>>((
        request: CallableRequest<UserActionRequest>,
    ): Promise<boolean> => {
        if (request.auth === undefined) {
            throwFunctionsHttpsError("unauthenticated", "User is not authenticated");
        }
        return cancelReservationHandler(request.data);
    });

export const testCancelReservation = onRequest((req, res) => {
    return cancelReservationHandler(
        UserActionRequest.newInstance(
            "sI2wbdRqYtdgArsq678BFSGDwr43",
            undefined,
            UserStatusType.None,
            {"storeId": "i9sAij5mVBijR85hgraE", "sectionId": "FMLYWLzKmiou1PTcrFR8", "seatId": "ZlblGsMYd7IlO1DEho4H"},
            undefined,
            undefined,
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
    onCall<UserActionRequest, Promise<boolean>>((
        request: CallableRequest<UserActionRequest>,
    ): Promise<boolean> => {
        if (request.auth === undefined) {
            throwFunctionsHttpsError("unauthenticated", "User is not authenticated");
        }
        return occupySeatHandler(request.data);
    });

export const testOccupySeat = onRequest((req, res) => {
    return occupySeatHandler(
        UserActionRequest.newInstance(
            "sI2wbdRqYtdgArsq678BFSGDwr43",
            undefined,
            UserStatusType.Occupied,
            {"storeId": "i9sAij5mVBijR85hgraE", "sectionId": "FMLYWLzKmiou1PTcrFR8", "seatId": "ZlblGsMYd7IlO1DEho4H"},
            1000,
            undefined,
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
    onCall<UserActionRequest, Promise<boolean>>((
        request: CallableRequest<UserActionRequest>,
    ): Promise<boolean> => {
        if (request.auth === undefined) {
            throwFunctionsHttpsError("unauthenticated", "User is not authenticated");
        }
        return stopUsingSeatHandler(request.data);
    });


export const testStopUsingSeat = onRequest((req, res) => {
    return stopUsingSeatHandler(
        UserActionRequest.newInstance(
            "sI2wbdRqYtdgArsq678BFSGDwr43",
            undefined,
            UserStatusType.None,
            {"storeId": "i9sAij5mVBijR85hgraE", "sectionId": "FMLYWLzKmiou1PTcrFR8", "seatId": "ZlblGsMYd7IlO1DEho4H"},
            undefined,
            undefined,
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
    onCall<UserActionRequest, Promise<boolean>>((
        request: CallableRequest<UserActionRequest>,
    ): Promise<boolean> => {
        if (request.auth === undefined) {
            throwFunctionsHttpsError("unauthenticated", "User is not authenticated");
        }
        return goVacantHandler(request.data);
    });

export const testGoVacant = onRequest((req, res) => {
    return goVacantHandler(
        UserActionRequest.newInstance(
            "sI2wbdRqYtdgArsq678BFSGDwr43",
            undefined,
            UserStatusType.Vacant,
            {"storeId": "i9sAij5mVBijR85hgraE", "sectionId": "FMLYWLzKmiou1PTcrFR8", "seatId": "ZlblGsMYd7IlO1DEho4H"},
            100,
            undefined,
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
    onCall<UserActionRequest, Promise<boolean>>((
        request: CallableRequest<UserActionRequest>,
    ): Promise<boolean> => {
        if (request.auth === undefined) {
            throwFunctionsHttpsError("unauthenticated", "User is not authenticated");
        }
        return goTaskHandler(request.data);
    });


export const testGoTask = onRequest((req, res) => {
    return goTaskHandler(
        UserActionRequest.newInstance(
            "sI2wbdRqYtdgArsq678BFSGDwr43",
            undefined,
            UserStatusType.OnTask,
            {"storeId": "i9sAij5mVBijR85hgraE", "sectionId": "FMLYWLzKmiou1PTcrFR8", "seatId": "ZlblGsMYd7IlO1DEho4H"},
            100,
            undefined,
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
import {timeoutOnTaskHandler} from "./handle_timeout/timeout_on_task";

export const helloWorld =
    onRequest(helloWorldHandler);

export const timeoutOnReserve =
    onRequest(timeoutOnReserveHandler);

export const timeoutOnReachUsageLimit =
    onRequest(timeoutOnReachUsageLimitHandler);


export const timeoutOnVacant =
    onRequest(timeoutOnVacantHandler);

export const timeoutOnTask =
    onRequest(timeoutOnTaskHandler);

/**
 * Triggered functions
 */

import {countSeatChangeHandler} from "./firestore/count_seat_change";
import {countSectionChangeHandler} from "./firestore/count_section_change";
import {throwFunctionsHttpsError} from "./util/functions_helper";

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

