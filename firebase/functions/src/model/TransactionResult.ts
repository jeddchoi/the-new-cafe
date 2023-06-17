export class TransactionResult<T> {
    constructor(
        public rollback?: () => Promise<unknown>,
        public after?: T | null,
        public committed: boolean = false,
    ) {
    }
}
