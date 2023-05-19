import * as functions from "firebase-functions";
import {FunctionsErrorCode} from "firebase-functions/lib/common/providers/https";

/**
 * Throw a functions.https.HttpsError
 * @param {FunctionsErrorCode} code
 * @param {string} errorMessage
 */
export function throwFunctionsHttpsError(code: FunctionsErrorCode, errorMessage: string): never {
    functions.logger.error(errorMessage);
    throw new functions.https.HttpsError(code, errorMessage);
}
