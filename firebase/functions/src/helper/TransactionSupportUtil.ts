import {TransactionResult} from "./TransactionResult";

export interface TransactionSupportUtil {
    transaction<T>(refPath: string, checkAndUpdate: (existing: T | null) => T | null): Promise<TransactionResult<T>>;
}
