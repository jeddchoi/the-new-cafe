import {ISeatPosition, UserStatusChangeReason, UserStatusType} from "../UserStatus";
import {Request} from "./Request";

export class UserActionRequest extends Request {
    static fromPaylod(payload: {
        userId: string,
        targetStatusType: UserStatusType,
        seatPosition: ISeatPosition,
        durationInSeconds?: number | undefined,
        until?: number | undefined,
    }): UserActionRequest {
        return new UserActionRequest(
            payload.seatPosition,
            payload.userId,
            payload.targetStatusType,
            payload.durationInSeconds,
            payload.until,
        );
    }

    constructor(
        readonly seatPosition: ISeatPosition,
        userId: string,
        targetStatusType: UserStatusType,
        durationInSeconds?: number | undefined,
        until?: number | undefined,
    ) {
        super(userId, targetStatusType, UserStatusChangeReason.UserAction, durationInSeconds, until);
    }

    toString() {
        const properties = Object.entries(this)
            .map(([key, value]) => `${key}: ${JSON.stringify(value)}`)
            .join(", ");
        return `UserActionRequest ${new Date(this.requestedAt).toISOString()} { ${properties} }`;
    }
}


