import {Request} from "./Request";
import {UserStatusChangeReason, UserStatusType} from "../UserStatus";

export class ManageUserRequest extends Request {
    static fromPaylod(payload: {
        userId: string,
        targetStatusType: UserStatusType,
        durationInSeconds?: number | undefined,
        until?: number | undefined,
    }): ManageUserRequest {
        return new ManageUserRequest(
            payload.userId,
            payload.targetStatusType,
            payload.durationInSeconds,
            payload.until,
        );
    }

    constructor(
        userId: string,
        targetStatusType: UserStatusType,
        durationInSeconds?: number | undefined,
        until?: number | undefined,
    ) {
        super(userId, targetStatusType, UserStatusChangeReason.Admin, durationInSeconds, until);
    }

    toString() {
        const properties = Object.entries(this)
            .map(([key, value]) => `${key}: ${JSON.stringify(value)}`)
            .join(", ");
        return `ManageUserRequest ${new Date(this.requestedAt).toISOString()} { ${properties} }`;
    }
}
