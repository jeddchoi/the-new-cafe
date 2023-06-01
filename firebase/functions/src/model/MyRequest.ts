import {ISeatPosition, UserStatusChangeReason, UserStatusType} from "./UserStatus";


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
    TransferToBusiness,
    LeaveAway,
    FinishBusiness,
    ResumeUsing,
    AddCurrentExtraTime,
    AddUsageExtraTime,
    TimeoutOnReserve,
    TimeoutOnUse,
    TimeoutOnBusiness,
    TimeoutOnAway,
    Block,
    Unblock,
    NoOp,
}


export const RequestTypeInfo: {
    [key in RequestType]: {
        availablePriorStatus: UserStatusType[],
        targetStatus: UserStatusType | "Existing Status",
        tasks: TaskType[],
        requireSeatPosition: "Request" | "Existing Status if not None" | undefined,
    }
} = {
    [RequestType.ReserveSeat]: {
        availablePriorStatus: [UserStatusType.None],
        targetStatus: UserStatusType.Reserved,
        tasks: [TaskType.UpdateSeatStatus, TaskType.StartCurrentTimer, TaskType.UpdateUserStatus],
        requireSeatPosition: "Request",
    },
    [RequestType.OccupySeat]: {
        availablePriorStatus: [UserStatusType.Reserved],
        targetStatus: UserStatusType.Occupied,
        tasks: [TaskType.StopCurrentTimer, TaskType.StartUsageTimer, TaskType.UpdateUserStatus, TaskType.UpdateSeatStatus],
        requireSeatPosition: "Existing Status if not None",
    },
    [RequestType.CancelReservation]: {
        availablePriorStatus: [UserStatusType.Reserved],
        targetStatus: UserStatusType.None,
        tasks: [TaskType.StopCurrentTimer, TaskType.UpdateUserStatus, TaskType.UpdateSeatStatus],
        requireSeatPosition: "Existing Status if not None",
    },
    [RequestType.StopUsingSeat]: {
        availablePriorStatus: [UserStatusType.Occupied, UserStatusType.OnBusiness, UserStatusType.Away],
        targetStatus: UserStatusType.None,
        tasks: [TaskType.StopCurrentTimer, TaskType.StopUsageTimer, TaskType.UpdateUserStatus, TaskType.UpdateSeatStatus],
        requireSeatPosition: "Existing Status if not None",
    },
    [RequestType.DoBusiness]: {
        availablePriorStatus: [UserStatusType.Occupied],
        targetStatus: UserStatusType.OnBusiness,
        tasks: [TaskType.StartCurrentTimer, TaskType.UpdateUserStatus, TaskType.UpdateSeatStatus],
        requireSeatPosition: "Existing Status if not None",
    },
    [RequestType.TransferToBusiness]: {
        availablePriorStatus: [UserStatusType.Away],
        targetStatus: UserStatusType.OnBusiness,
        tasks: [TaskType.StopCurrentTimer, TaskType.StartCurrentTimer, TaskType.UpdateUserStatus],
        requireSeatPosition: undefined,
    },
    [RequestType.LeaveAway]: {
        availablePriorStatus: [UserStatusType.Occupied],
        targetStatus: UserStatusType.Away,
        tasks: [TaskType.StartCurrentTimer, TaskType.UpdateUserStatus, TaskType.UpdateSeatStatus],
        requireSeatPosition: "Existing Status if not None",
    },
    [RequestType.FinishBusiness]: {
        availablePriorStatus: [UserStatusType.OnBusiness],
        targetStatus: UserStatusType.Away,
        tasks: [TaskType.StopCurrentTimer, TaskType.StartCurrentTimer, TaskType.UpdateUserStatus],
        requireSeatPosition: undefined,
    },
    [RequestType.ResumeUsing]: {
        availablePriorStatus: [UserStatusType.Away],
        targetStatus: UserStatusType.Occupied,
        tasks: [TaskType.StopCurrentTimer, TaskType.UpdateUserStatus, TaskType.UpdateSeatStatus],
        requireSeatPosition: "Existing Status if not None",
    },
    [RequestType.AddCurrentExtraTime]: {
        availablePriorStatus: [UserStatusType.Reserved, UserStatusType.Away, UserStatusType.OnBusiness],
        targetStatus: "Existing Status",
        tasks: [TaskType.StopCurrentTimer, TaskType.StartCurrentTimer, TaskType.UpdateUserStatus],
        requireSeatPosition: undefined,
    },
    [RequestType.AddUsageExtraTime]: {
        availablePriorStatus: [UserStatusType.Occupied],
        targetStatus: "Existing Status",
        tasks: [TaskType.StopUsageTimer, TaskType.StartUsageTimer, TaskType.UpdateUserStatus],
        requireSeatPosition: undefined,
    },
    [RequestType.TimeoutOnReserve]: {
        availablePriorStatus: [UserStatusType.Reserved],
        targetStatus: UserStatusType.None,
        tasks: [TaskType.UpdateUserStatus, TaskType.UpdateSeatStatus],
        requireSeatPosition: "Existing Status if not None",
    },
    [RequestType.TimeoutOnUse]: {
        availablePriorStatus: [UserStatusType.Occupied],
        targetStatus: UserStatusType.None,
        tasks: [TaskType.StopCurrentTimer, TaskType.UpdateUserStatus, TaskType.UpdateSeatStatus],
        requireSeatPosition: "Existing Status if not None",
    },
    [RequestType.TimeoutOnBusiness]: {
        availablePriorStatus: [UserStatusType.OnBusiness],
        targetStatus: UserStatusType.Away,
        tasks: [TaskType.StartCurrentTimer, TaskType.UpdateUserStatus],
        requireSeatPosition: undefined,
    },
    [RequestType.TimeoutOnAway]: {
        availablePriorStatus: [UserStatusType.Away],
        targetStatus: UserStatusType.None,
        tasks: [TaskType.StopUsageTimer, TaskType.UpdateUserStatus, TaskType.UpdateSeatStatus],
        requireSeatPosition: "Existing Status if not None",
    },
    [RequestType.Block]: {
        availablePriorStatus: [UserStatusType.None, UserStatusType.Reserved, UserStatusType.Occupied, UserStatusType.Away, UserStatusType.OnBusiness],
        targetStatus: UserStatusType.Blocked,
        tasks: [TaskType.StopCurrentTimer, TaskType.StopUsageTimer, TaskType.StartCurrentTimer, TaskType.UpdateUserStatus, TaskType.UpdateSeatStatus],
        requireSeatPosition: "Existing Status if not None",
    },
    [RequestType.Unblock]: {
        availablePriorStatus: [UserStatusType.Blocked],
        targetStatus: UserStatusType.None,
        tasks: [TaskType.StopCurrentTimer, TaskType.UpdateUserStatus],
        requireSeatPosition: undefined,
    },
    [RequestType.NoOp]: {
        availablePriorStatus: [UserStatusType.None, UserStatusType.Reserved, UserStatusType.Occupied, UserStatusType.OnBusiness, UserStatusType.Away, UserStatusType.Blocked],
        targetStatus: "Existing Status",
        tasks: [],
        requireSeatPosition: undefined,
    },
};


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
