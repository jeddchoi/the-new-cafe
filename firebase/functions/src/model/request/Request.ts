import {UserStatusChangeReason, UserStatusType} from "../UserStatus";


export enum TaskType {
    StopCurrentTimer,
    StopUsageTimer,
    StartCurrentTimer,
    StartUsageTimer,
    UpdateUserStatus,
    UpdateSeatStatus,
}

export enum RequestType {
    ReserveSeat,
    OccupySeat,
    CancelReservation,
    StopUsingSeat,
    DoBusiness,

}
export const RequestTypeInfo = {

};

export interface DeadlineInfo {
    keepStatusUntil: number,
    durationInSeconds: number,
}

export abstract class Request {
    protected constructor(
        readonly userId: string,
        readonly createdAt: number,
        readonly startStatusAt: number,
        readonly deadlineInfo: DeadlineInfo | undefined, // if undefined, no deadline
        readonly targetStatusType: UserStatusType,
        readonly reason: UserStatusChangeReason,
    ) {
    }
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
