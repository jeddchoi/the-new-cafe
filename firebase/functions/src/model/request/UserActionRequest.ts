import {ISeatPosition, UserStatusChangeReason, UserStatusType} from "../UserStatus";
import {DeadlineInfo, getDeadline, Request} from "./Request";
import {logger} from "firebase-functions/v2";


export class UserActionRequest extends Request {
    static fromPayload(payload: {
        userId: string,
        createdAt: number,
        startStatusAt: number,
        deadlineInfo: DeadlineInfo | undefined,
        targetStatusType: UserStatusType,
        reason: UserStatusChangeReason,
        seatPosition: ISeatPosition,
    }): UserActionRequest {
        logger.info(`fromPayload = ${JSON.stringify(payload)}`);
        return new UserActionRequest(
            payload.userId,
            payload.createdAt,
            payload.startStatusAt,
            payload.deadlineInfo,
            payload.targetStatusType,
            payload.reason,
            payload.seatPosition,
        );
    }

    static newInstance(
        userId: string,
        startStatusAt: number | undefined,
        targetStatusType: UserStatusType,
        seatPosition: ISeatPosition,
        durationInSeconds ?: number,
        keepStatusUntil ?: number,
    ): UserActionRequest {
        const current = new Date().getTime();
        const startingTime = startStatusAt ?? current;
        const deadlineInfo = getDeadline(startingTime, durationInSeconds, keepStatusUntil);

        return new UserActionRequest(
            userId,
            current,
            startingTime,
            deadlineInfo,
            targetStatusType,
            UserStatusChangeReason.UserAction,
            seatPosition,
        );
    }

    private constructor(
        readonly userId: string,
        readonly createdAt: number,
        readonly startStatusAt: number,
        readonly deadlineInfo: DeadlineInfo | undefined,
        readonly targetStatusType: UserStatusType,
        readonly reason: UserStatusChangeReason,
        readonly seatPosition: ISeatPosition,
    ) {
        super(userId, createdAt, startStatusAt, deadlineInfo, targetStatusType, reason);
    }

    toString() {
        const properties = Object.entries(this)
            .map(([key, value]) => `${key}: ${JSON.stringify(value)}`)
            .join(", ");
        return `UserActionRequest ${new Date(this.createdAt).toISOString()} { ${properties} }`;
    }
}


