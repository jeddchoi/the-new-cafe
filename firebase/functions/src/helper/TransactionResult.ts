export class TransactionResult<T> {
    constructor(
        public before: T | null = null,
        public after: T | null = null,
        public committed: boolean = false,
        public rollback?: () => Promise<unknown>,
    ) {
    }
}
