import {https} from "firebase-functions/v2";

export class TransactionResult<T> {
    constructor(
        public before: T | null = null,
        public after: T | null = null,
        public rollback: () => Promise<void> = () => Promise.reject(new https.HttpsError("unknown", "rollback is not defined")),
    ) {
    }
}
