import {DeadlineInfo, getDeadline, Request} from "./Request";
import {ISeatPosition, UserStatusChangeReason, UserStatusType} from "../UserStatus";
import {logger} from "firebase-functions/v2";

export class TimeoutRequest extends Request {
    static fromPayload(payload: {
        userId: string,
        createdAt: number,
        startStatusAt: number,
        deadlineInfo: DeadlineInfo | undefined,
        targetStatusType: UserStatusType,
        reason: UserStatusChangeReason,
        seatPosition: ISeatPosition,
        invokeFunctionName: string,
    }): TimeoutRequest {
        logger.info(`fromPayload = ${JSON.stringify(payload)}`);
        return new TimeoutRequest(
            payload.userId,
            payload.createdAt,
            payload.startStatusAt,
            payload.deadlineInfo,
            payload.targetStatusType,
            payload.reason,
            payload.seatPosition,
            payload.invokeFunctionName,
        );
    }

    static newInstance(
        userId: string,
        startStatusAt: number | undefined,
        targetStatusType: UserStatusType,
        seatPosition: ISeatPosition,
        invokeFunctionName: string,
        durationInSeconds ?: number,
        keepStatusUntil ?: number,
    ): TimeoutRequest {
        const current = new Date().getTime();
        const startingTime = startStatusAt ?? current;
        const deadlineInfo = getDeadline(startingTime, durationInSeconds, keepStatusUntil);

        return new TimeoutRequest(
            userId,
            current,
            startingTime,
            deadlineInfo,
            targetStatusType,
            UserStatusChangeReason.Timeout,
            seatPosition,
            invokeFunctionName,
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
        readonly invokeFunctionName: string,
    ) {
        super(userId, createdAt, startStatusAt, deadlineInfo, targetStatusType, reason);
    }

    toString() {
        const properties = Object.entries(this)
            .map(([key, value]) => `${key}: ${JSON.stringify(value)}`)
            .join(", ");
        return `TimeoutRequest ${new Date(this.createdAt).toISOString()} { ${properties} }`;
    }
}

