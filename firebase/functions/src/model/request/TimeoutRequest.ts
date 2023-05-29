import {Request} from "./Request";
import {ISeatPosition, UserStatusChangeReason, UserStatusType} from "../UserStatus";

export class TimeoutRequest extends Request {
    static fromPaylod(payload: {
        userId: string,
        targetStatusType: UserStatusType,
        seatPosition: ISeatPosition,
        durationInSeconds?: number | undefined,
        until?: number | undefined,
    }): TimeoutRequest {
        return new TimeoutRequest(
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
        super(userId, targetStatusType, UserStatusChangeReason.Timeout, durationInSeconds, until);
    }

    toString() {
        const properties = Object.entries(this)
            .map(([key, value]) => `${key}: ${JSON.stringify(value)}`)
            .join(", ");
        return `TimeoutRequest ${new Date(this.requestedAt).toISOString()} { ${properties} }`;
    }
}

