import {ResultCode} from "../seat-finder/_enum/ResultCode";

export function isResultCode(error: any): error is ResultCode {
    return Object.values(ResultCode).includes(error);
}
