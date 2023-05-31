import {DeadlineInfo, getDeadline, Request} from "./Request";
import {ISeatPosition, UserStatusChangeReason, UserStatusType} from "../UserStatus";
import {logger} from "firebase-functions/v2";

export class ManageUserRequest extends Request {
    static fromPayload(payload: {
        userId: string,
        createdAt: number,
        startStatusAt: number,
        deadlineInfo: DeadlineInfo | undefined,
        targetStatusType: UserStatusType,
        reason: UserStatusChangeReason,
    }): ManageUserRequest {
        logger.info(`fromPayload = ${JSON.stringify(payload)}`);
        return new ManageUserRequest(
            payload.userId,
            payload.createdAt,
            payload.startStatusAt,
            payload.deadlineInfo,
            payload.targetStatusType,
            payload.reason,
        );
    }

    static newInstance(
        userId: string,
        startStatusAt: number | undefined,
        targetStatusType: UserStatusType,
        durationInSeconds ?: number,
        keepStatusUntil ?: number,
    ): ManageUserRequest {
        const current = new Date().getTime();
        const startingTime = startStatusAt ?? current;
        const deadlineInfo = getDeadline(startingTime, durationInSeconds, keepStatusUntil);

        return new ManageUserRequest(
            userId,
            current,
            startingTime,
            deadlineInfo,
            targetStatusType,
            UserStatusChangeReason.Admin,
        );
    }

    private constructor(
        readonly userId: string,
        readonly createdAt: number,
        readonly startStatusAt: number,
        readonly deadlineInfo: DeadlineInfo | undefined,
        readonly targetStatusType: UserStatusType,
        readonly reason: UserStatusChangeReason,
    ) {
        super(userId, createdAt, startStatusAt, deadlineInfo, targetStatusType, reason);
    }

    toString() {
        const properties = Object.entries(this)
            .map(([key, value]) => `${key}: ${JSON.stringify(value)}`)
            .join(", ");
        return `ManageUserRequest ${new Date(this.createdAt).toISOString()} { ${properties} }`;
    }
}
