import {TaskType} from "./TaskType";
import {RequestType} from "./RequestType";
import {UserStatusType} from "./UserStatusType";

export const RequestTypeInfo: {
    [key in RequestType]: {
        availablePriorStatus: UserStatusType[],
        targetStatus: UserStatusType | "Existing Status",
        tasks: TaskType[],
        requireSeatPosition: "Request" | "Existing Status if not None" | undefined,
        isTimeoutRequest: boolean,
    }
} = {
    [RequestType.ReserveSeat]: {
        availablePriorStatus: [UserStatusType.None],
        targetStatus: UserStatusType.Reserved,
        tasks: [TaskType.UpdateSeatStatus, TaskType.StartCurrentTimer, TaskType.UpdateUserStatus],
        requireSeatPosition: "Request",
        isTimeoutRequest: false,
    },
    [RequestType.OccupySeat]: {
        availablePriorStatus: [UserStatusType.Reserved],
        targetStatus: UserStatusType.Occupied,
        tasks: [TaskType.StopCurrentTimer, TaskType.StartUsageTimer, TaskType.UpdateUserStatus, TaskType.UpdateSeatStatus],
        requireSeatPosition: "Existing Status if not None",
        isTimeoutRequest: false,
    },
    [RequestType.CancelReservation]: {
        availablePriorStatus: [UserStatusType.Reserved],
        targetStatus: UserStatusType.None,
        tasks: [TaskType.StopCurrentTimer, TaskType.UpdateUserStatus, TaskType.UpdateSeatStatus],
        requireSeatPosition: "Existing Status if not None",
        isTimeoutRequest: false,
    },
    [RequestType.StopUsingSeat]: {
        availablePriorStatus: [UserStatusType.Occupied, UserStatusType.OnBusiness, UserStatusType.Away],
        targetStatus: UserStatusType.None,
        tasks: [TaskType.StopCurrentTimer, TaskType.StopUsageTimer, TaskType.UpdateUserStatus, TaskType.UpdateSeatStatus],
        requireSeatPosition: "Existing Status if not None",
        isTimeoutRequest: false,
    },
    [RequestType.DoBusiness]: {
        availablePriorStatus: [UserStatusType.Occupied],
        targetStatus: UserStatusType.OnBusiness,
        tasks: [TaskType.StartCurrentTimer, TaskType.UpdateUserStatus, TaskType.UpdateSeatStatus],
        requireSeatPosition: "Existing Status if not None",
        isTimeoutRequest: false,
    },
    [RequestType.TransferToBusiness]: {
        availablePriorStatus: [UserStatusType.Away],
        targetStatus: UserStatusType.OnBusiness,
        tasks: [TaskType.StopCurrentTimer, TaskType.StartCurrentTimer, TaskType.UpdateUserStatus],
        requireSeatPosition: undefined,
        isTimeoutRequest: false,
    },
    [RequestType.LeaveAway]: {
        availablePriorStatus: [UserStatusType.Occupied],
        targetStatus: UserStatusType.Away,
        tasks: [TaskType.StartCurrentTimer, TaskType.UpdateUserStatus, TaskType.UpdateSeatStatus],
        requireSeatPosition: "Existing Status if not None",
        isTimeoutRequest: false,
    },
    [RequestType.FinishBusiness]: {
        availablePriorStatus: [UserStatusType.OnBusiness],
        targetStatus: UserStatusType.Away,
        tasks: [TaskType.StopCurrentTimer, TaskType.StartCurrentTimer, TaskType.UpdateUserStatus],
        requireSeatPosition: undefined,
        isTimeoutRequest: false,
    },
    [RequestType.ResumeUsing]: {
        availablePriorStatus: [UserStatusType.Away],
        targetStatus: UserStatusType.Occupied,
        tasks: [TaskType.StopCurrentTimer, TaskType.UpdateUserStatus, TaskType.UpdateSeatStatus],
        requireSeatPosition: "Existing Status if not None",
        isTimeoutRequest: false,
    },
    [RequestType.ChangeCurrentTimeoutTime]: {
        availablePriorStatus: [UserStatusType.Reserved, UserStatusType.Away, UserStatusType.OnBusiness],
        targetStatus: "Existing Status",
        tasks: [TaskType.StopCurrentTimer, TaskType.StartCurrentTimer, TaskType.UpdateUserStatus],
        requireSeatPosition: undefined,
        isTimeoutRequest: false,
    },
    [RequestType.ChangeUsageTimeoutTime]: {
        availablePriorStatus: [UserStatusType.Occupied],
        targetStatus: "Existing Status",
        tasks: [TaskType.StopUsageTimer, TaskType.StartUsageTimer, TaskType.UpdateUserStatus],
        requireSeatPosition: undefined,
        isTimeoutRequest: false,
    },
    [RequestType.TimeoutOnReserve]: {
        availablePriorStatus: [UserStatusType.Reserved],
        targetStatus: UserStatusType.None,
        tasks: [TaskType.StopCurrentTimer, TaskType.UpdateUserStatus, TaskType.UpdateSeatStatus],
        requireSeatPosition: "Existing Status if not None",
        isTimeoutRequest: true,
    },
    [RequestType.TimeoutOnUse]: {
        availablePriorStatus: [UserStatusType.Occupied],
        targetStatus: UserStatusType.None,
        tasks: [TaskType.StopCurrentTimer, TaskType.StopUsageTimer, TaskType.UpdateUserStatus, TaskType.UpdateSeatStatus],
        requireSeatPosition: "Existing Status if not None",
        isTimeoutRequest: true,
    },
    [RequestType.TimeoutOnBusiness]: {
        availablePriorStatus: [UserStatusType.OnBusiness],
        targetStatus: UserStatusType.Away,
        tasks: [TaskType.StopCurrentTimer, TaskType.StartCurrentTimer, TaskType.UpdateUserStatus],
        requireSeatPosition: undefined,
        isTimeoutRequest: true,
    },
    [RequestType.TimeoutOnAway]: {
        availablePriorStatus: [UserStatusType.Away],
        targetStatus: UserStatusType.None,
        tasks: [TaskType.StopCurrentTimer, TaskType.StopUsageTimer, TaskType.UpdateUserStatus, TaskType.UpdateSeatStatus],
        requireSeatPosition: "Existing Status if not None",
        isTimeoutRequest: true,
    },
    [RequestType.TimeoutOnBlock]: {
        availablePriorStatus: [UserStatusType.Blocked],
        targetStatus: UserStatusType.None,
        tasks: [TaskType.StopCurrentTimer, TaskType.UpdateUserStatus],
        requireSeatPosition: undefined,
        isTimeoutRequest: true,
    },
    [RequestType.Block]: {
        availablePriorStatus: [UserStatusType.None, UserStatusType.Reserved, UserStatusType.Occupied, UserStatusType.Away, UserStatusType.OnBusiness],
        targetStatus: UserStatusType.Blocked,
        tasks: [TaskType.StopCurrentTimer, TaskType.StopUsageTimer, TaskType.StartCurrentTimer, TaskType.UpdateUserStatus, TaskType.UpdateSeatStatus],
        requireSeatPosition: "Existing Status if not None",
        isTimeoutRequest: false,
    },
    [RequestType.Unblock]: {
        availablePriorStatus: [UserStatusType.Blocked],
        targetStatus: UserStatusType.None,
        tasks: [TaskType.StopCurrentTimer, TaskType.UpdateUserStatus],
        requireSeatPosition: undefined,
        isTimeoutRequest: false,
    },
    [RequestType.NoOp]: {
        availablePriorStatus: [UserStatusType.None, UserStatusType.Reserved, UserStatusType.Occupied, UserStatusType.OnBusiness, UserStatusType.Away, UserStatusType.Blocked],
        targetStatus: "Existing Status",
        tasks: [],
        requireSeatPosition: undefined,
        isTimeoutRequest: false,
    },
};
