import {ISeatPosition} from "./UserStatus";
import {RequestType} from "./RequestType";
import {UserStatusChangeReason} from "./UserStatusChangeReason";


export interface DeadlineInfo {
    keepStatusUntil: number,
    durationInSeconds: number,
}

export class MyRequest {
    static newInstance(
        requestType: RequestType,
        userId: string,
        reason: UserStatusChangeReason,
        startStatusAt: number | undefined,
        seatPosition: ISeatPosition | undefined,
        durationInSeconds ?: number,
        keepStatusUntil ?: number,
    ): MyRequest {
        const current = new Date().getTime();
        const startingTime = startStatusAt ?? current;
        const deadlineInfo = getDeadline(startingTime, durationInSeconds, keepStatusUntil);

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
        readonly startStatusAt: number,
        readonly reason: UserStatusChangeReason,
        readonly deadlineInfo: DeadlineInfo | undefined, // if undefined, no deadline
        readonly seatPosition: ISeatPosition | undefined,
    ) {
    }

    toString = () => JSON.stringify(this, null, " ");
}


export function getDeadline(startingTime: number, durationInSeconds: number | undefined, keepStatusUntil: number | undefined): DeadlineInfo | undefined {
    if (durationInSeconds === undefined && keepStatusUntil === undefined) {
        return undefined;
    } else if (durationInSeconds !== undefined && keepStatusUntil === undefined) {
        return <DeadlineInfo>{
            durationInSeconds,
            keepStatusUntil: startingTime + durationInSeconds * 1000,
        };
    } else if (durationInSeconds === undefined && keepStatusUntil !== undefined) {
        return <DeadlineInfo>{
            durationInSeconds: Math.round((keepStatusUntil - startingTime) / 1000),
            keepStatusUntil,
        };
    } else {
        return <DeadlineInfo>{
            durationInSeconds,
            keepStatusUntil,
        };
    }
}
