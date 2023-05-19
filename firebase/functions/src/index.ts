import * as functions from "firebase-functions";
import * as admin from "firebase-admin";

admin.initializeApp();

import {helloWorldHandler} from "./on-request/hello_world";
import {reserveSeatHandler} from "./on-call/on_reserve";
import {
    COLLECTION_GROUP_SEAT_NAME,
    COLLECTION_GROUP_SECTION_NAME,
    COLLECTION_GROUP_STORE_NAME,
} from "./util/FirestoreUtil";
import {countSeatChangeHandler} from "./firestore/count_seat_change";
import {countSectionChangeHandler} from "./firestore/count_section_change";


export const helloWorld = functions
    // .runWith({})
    .region("us-central1")
    .https.onRequest(helloWorldHandler);


export const reserveSeat = functions
    .region("us-central1")
    .https.onCall(reserveSeatHandler);


export const countSeatChange = functions
    .region("us-central1").firestore
    .document(`${COLLECTION_GROUP_STORE_NAME}/{storeId}/${COLLECTION_GROUP_SECTION_NAME}/{sectionId}/${COLLECTION_GROUP_SEAT_NAME}/{seatId}`)
    .onWrite(countSeatChangeHandler);

export const countSectionChange = functions
    .region("us-central1").firestore
    .document(`${COLLECTION_GROUP_STORE_NAME}/{storeId}/${COLLECTION_GROUP_SECTION_NAME}/{sectionId}`)
    .onWrite(countSectionChangeHandler);
