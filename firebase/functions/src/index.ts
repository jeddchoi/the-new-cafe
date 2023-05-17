import * as functions from "firebase-functions";
import * as admin from "firebase-admin";
import {helloWorldHandler} from "./on-request/helloWorldHandler";

admin.initializeApp();

export const helloWorld = functions
    .region("us-central1")
    .https.onRequest(helloWorldHandler);
