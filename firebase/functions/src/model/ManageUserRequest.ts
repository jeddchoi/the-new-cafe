
enum UserManageType {
    Block,
    Unblock,
}

export class ManageUserRequest {
    constructor(
        readonly userId: string,
        readonly userManageType: UserManageType,
        readonly durationInSeconds?: number | undefined,
        readonly until?: number | undefined,
    ) {
    }
}
