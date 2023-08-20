import {ResultCode} from "../seat-finder/_enum/ResultCode";

export function isResultCode(value: unknown): value is ResultCode {
    return typeof value === "string" && (Object.values(ResultCode).find((code) => {
        return value === code;
    }) !== undefined);
}
