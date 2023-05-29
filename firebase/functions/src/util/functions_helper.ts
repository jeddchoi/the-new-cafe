import {logger, https} from "firebase-functions/v2";
import {FunctionsErrorCode} from "firebase-functions/lib/common/providers/https";

/**
 * Throw a functions.https.HttpsError
 * @param {FunctionsErrorCode} code
 * @param {string} errorMessage
 */
export function throwFunctionsHttpsError(code: FunctionsErrorCode, errorMessage: string): never {
    logger.error(errorMessage);
    throw new https.HttpsError(code, errorMessage);
}
