import {https} from "firebase-functions/v2";
import {ResultCode} from "../seat-finder/_enum/ResultCode";

export class TransactionResult<T> {
    constructor(
        readonly before: T | null,
        readonly after: T | null,
        readonly rollback: () => Promise<void> = () => Promise.reject(new https.HttpsError("unknown", "rollback is not defined")),
        readonly resultCode: ResultCode = ResultCode.REJECTED,
    ) {
    }
}
