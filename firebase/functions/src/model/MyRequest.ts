import {ISeatPosition} from "./UserState";
import {RequestType} from "./RequestType";
import {UserStateChangeReason} from "./UserStateChangeReason";


export interface DeadlineInfo {
    keepStateUntil: number,
    durationInSeconds: number,
}

export class MyRequest {
    static newInstance(
        requestType: RequestType,
        userId: string,
        reason: UserStateChangeReason,
        startStateAt: number | undefined,
        seatPosition: ISeatPosition | undefined,
        durationInSeconds ?: number,
        keepStateUntil ?: number,
    ): MyRequest {
        const current = new Date().getTime();
        const startingTime = startStateAt ?? current;
        const deadlineInfo = getDeadline(startingTime, durationInSeconds, keepStateUntil);

        return new MyRequest(
            requestType,
            userId,
            current,
            startingTime,
            reason,
            deadlineInfo,
            seatPosition,
        );
    }

    private constructor(
        readonly requestType: RequestType,
        readonly userId: string,
        readonly createdAt: number,
        readonly startStateAt: number,
        readonly reason: UserStateChangeReason,
        readonly deadlineInfo: DeadlineInfo | undefined, // if undefined, no deadline
        readonly seatPosition: ISeatPosition | undefined,
    ) {
    }
}


export function getDeadline(startingTime: number, durationInSeconds: number | undefined, keepStateUntil: number | undefined): DeadlineInfo | undefined {
    if (durationInSeconds !== undefined) {
        return <DeadlineInfo>{
            durationInSeconds,
            keepStateUntil: startingTime + durationInSeconds * 1000,
        };
    }
    if (keepStateUntil !== undefined) {
        return <DeadlineInfo>{
            durationInSeconds: Math.round((keepStateUntil - startingTime) / 1000),
            keepStateUntil,
        };
    }
    return undefined;
}
