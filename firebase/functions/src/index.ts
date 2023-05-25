import {initializeApp} from "firebase-admin/app";

initializeApp();

import {defineString} from "firebase-functions/params";

const httpOrCallableFunctionRegion = defineString("LOCATION_FUNCTION", {});
const realtimeDatabaseBackgroundFunctionRegion = defineString("LOCATION_REALTIME_DATABASE_BACKGROUND_FUNCTION");
const firestoreBackgroundFunctionRegion = defineString("LOCATION_FIRESTORE_BACKGROUND_FUNCTION");

import {onRequest, onCall} from "firebase-functions/v2/https";
import {onDocumentWritten} from "firebase-functions/v2/firestore";

import {helloWorldHandler} from "./on-request/hello_world";
import {reserveSeatHandler} from "./on-call/on_reserve";
import {
    COLLECTION_GROUP_SEAT_NAME,
    COLLECTION_GROUP_SECTION_NAME,
    COLLECTION_GROUP_STORE_NAME,
} from "./util/FirestoreUtil";
import {countSeatChangeHandler} from "./firestore/count_seat_change";
import {countSectionChangeHandler} from "./firestore/count_section_change";
import {UserSeatUpdateRequest} from "./model/UserSeatUpdateRequest";
import {CloudTasksUtil} from "./util/CloudTasksUtil";
import {ISeatPosition, UserStatusChangeReason, UserStatusType} from "./model/UserStatus";
import {logger} from "firebase-functions/v2";


export const helloWorld =
    onRequest(helloWorldHandler);


export const reserveSeat =
    onCall<UserSeatUpdateRequest, Promise<boolean>>(reserveSeatHandler);


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

