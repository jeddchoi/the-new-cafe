export class TransactionResult<T> {
    constructor(
        public rollback?: () => Promise<unknown>,
        public before?: T | null,
        public after?: T | null,
        public committed: boolean = false,
    ) {
    }
}
