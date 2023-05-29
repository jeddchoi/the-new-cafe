import {UserStatusChangeReason, UserStatusType} from "../UserStatus";
import {throwFunctionsHttpsError} from "../../util/functions_helper";

export abstract class Request {
    readonly requestedAt: number;
    readonly until: number;

    protected constructor(
        readonly userId: string,
        readonly targetStatusType: UserStatusType,
        readonly reason: UserStatusChangeReason,
        durationInSeconds?: number | undefined,
        until?: number | undefined,
    ) {
        this.requestedAt = new Date().getTime();
        this.until = getEta(this.requestedAt, durationInSeconds, until);
    }
}
function getEta(requestedAt: number, durationInSeconds: number | undefined, until: number | undefined) {
    if (durationInSeconds === undefined && until === undefined) {
        throwFunctionsHttpsError("invalid-argument", "Until or durationInSeconds is not provided");
    }
    if (durationInSeconds !== undefined && durationInSeconds < 0) {
        throwFunctionsHttpsError("invalid-argument", "DurationInSeconds must not be negative");
    }
    if (until !== undefined && until < requestedAt) {
        throwFunctionsHttpsError("invalid-argument", "Until must not be past than requestedAt");
    }
    return until ?? requestedAt + (durationInSeconds ?? 0) * 1000;
}

