import {ISeatPosition, UserStatusChangeReason, UserStatusType} from "./UserStatus";

export class UserSeatUpdateRequest {
    constructor(
        readonly userId: string,
        readonly targetStatusType: UserStatusType,
        readonly reason: UserStatusChangeReason,
        readonly seatPosition: ISeatPosition,
        readonly durationInSeconds?: number | undefined,
        readonly until?: number | undefined,
    ) {
    }
}
