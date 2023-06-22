import {ResultCode} from "../seat-finder/_enum/ResultCode";

export function isResultCode(error: any): error is ResultCode {
    return isSomeEnum(ResultCode)(error);
}

const isSomeEnum = <T>(e: T) => (token: any): token is T[keyof T] =>
    (Object as any).values(e).includes(token as T[keyof T]);
