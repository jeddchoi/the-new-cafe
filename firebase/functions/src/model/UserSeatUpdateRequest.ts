import {ISeatPosition, UserStatusChangeReason, UserStatusType} from "./UserStatus";

export class UserSeatUpdateRequest {
    constructor(
        readonly targetStatusType: UserStatusType,
        readonly reason: UserStatusChangeReason,
        readonly seatPosition: ISeatPosition | undefined,
        readonly durationInSeconds: number | undefined,
        readonly until: number | undefined,
    ) {
    }
}
