import {https, logger} from "firebase-functions/v2";
import {FunctionsErrorCode} from "firebase-functions/lib/common/providers/https";
import {BusinessResultCode} from "../model/BusinessResultCode";

/**
 * Throw a functions.https.HttpsError
 * @param {FunctionsErrorCode} code
 * @param {string} errorMessage
 */
export function throwFunctionsHttpsError(code: FunctionsErrorCode, errorMessage: string): never {
    logger.error(errorMessage);
    throw new https.HttpsError(code, errorMessage);
}


export function isBusinessResultCode(error: any): error is BusinessResultCode {
    return Object.values(BusinessResultCode).includes(error);
}
