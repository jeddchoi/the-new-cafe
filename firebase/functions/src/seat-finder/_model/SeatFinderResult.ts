import {TransactionResult} from "../../helper/TransactionResult";
import {CurrentSession} from "../../_database/model/CurrentSession";
import {Seat} from "../../_firestore/model/Seat";
import {logger} from "firebase-functions/v2";

export interface SeatFinderResult {
    sessionResult?: TransactionResult<CurrentSession>;
    seatResult?: TransactionResult<Seat>;
}

export function printDiff(seatFinderResult: SeatFinderResult) {
    logger.info("======== Seat Finder Result DIFF =========");
    logger.log("=============== Seat Diff ===============");
    compareObjects(seatFinderResult.seatResult?.before, seatFinderResult.seatResult?.after);
    logger.log("=============== Session Diff ===============");
    compareObjects(seatFinderResult.sessionResult?.before, seatFinderResult.sessionResult?.after);
}

function compareObjects<T>(obj1: T, obj2: T, path = "") {
    if (obj1 && obj2) {
        if (Object.getOwnPropertyNames(obj1).length >= Object.getOwnPropertyNames(obj2).length) {
        // eslint-disable-next-line guard-for-in
            for (const key in obj1) {
                const value1 = Object.prototype.hasOwnProperty.call(obj1, key) ? obj1[key as keyof T] : null;
                const value2 = Object.prototype.hasOwnProperty.call(obj2, key) ? obj2[key as keyof T] : null;
                const currentPath = path ? `${path}.${key}` : key.toString();

                if (typeof value1 === "object" && typeof value2 === "object") {
                    compareObjects(value1 as unknown, value2 as unknown, currentPath); // 재귀적으로 nested 프로퍼티 비교
                } else {
                    if (value1 !== value2) {
                        console.log(`[${currentPath}]: ${value1} => ${value2}`);
                    }
                }
            }
        } else {
            // eslint-disable-next-line guard-for-in
            for (const key in obj2) {
                const value1 = Object.prototype.hasOwnProperty.call(obj1, key) ? obj1[key as keyof T] : null;
                const value2 = Object.prototype.hasOwnProperty.call(obj2, key) ? obj2[key as keyof T] : null;
                const currentPath = path ? `${path}.${key}` : key.toString();

                if (typeof value1 === "object" && typeof value2 === "object") {
                    compareObjects(value1 as unknown, value2 as unknown, currentPath); // 재귀적으로 nested 프로퍼티 비교
                } else {
                    if (value1 !== value2) {
                        console.log(`[${currentPath}]: ${value1} => ${value2}`);
                    }
                }
            }
        }
    } else if (obj1 && !obj2) {
        // eslint-disable-next-line guard-for-in
        for (const key in obj1) {
            const value1 = Object.prototype.hasOwnProperty.call(obj1, key) ? obj1[key as keyof T] : null;
            const value2 = null;
            const currentPath = path ? `${path}.${key}` : key.toString();

            if (typeof value1 === "object" && typeof value2 === "object") {
                compareObjects(value1 as unknown, value2 as unknown, currentPath); // 재귀적으로 nested 프로퍼티 비교
            } else {
                if (value1 !== value2) {
                    console.log(`[${currentPath}]: ${value1} => ${value2}`);
                }
            }
        }
    } else if (!obj1 && obj2) {
        // eslint-disable-next-line guard-for-in
        for (const key in obj2) {
            const value1 = null;
            const value2 = Object.prototype.hasOwnProperty.call(obj2, key) ? obj2[key as keyof T] : null;
            const currentPath = path ? `${path}.${key}` : key.toString();

            if (typeof value1 === "object" && typeof value2 === "object") {
                compareObjects(value1 as unknown, value2 as unknown, currentPath); // 재귀적으로 nested 프로퍼티 비교
            } else {
                if (value1 !== value2) {
                    console.log(`[${currentPath}]: ${value1} => ${value2}`);
                }
            }
        }
    }
}

