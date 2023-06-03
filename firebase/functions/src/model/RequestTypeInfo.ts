import {TaskType} from "./TaskType";
import {RequestType} from "./RequestType";
import {UserStatusType} from "./UserStatusType";
import {SeatStatusType} from "./Seat";

export const RequestTypeInfo: {
    [key in RequestType]: {
        availablePriorUserStatus: UserStatusType[],
        targetStatus: UserStatusType | "Existing Status",
        tasks: TaskType[],
        requireSeatPosition: "Request" | "Existing Status if not None" | undefined,
    }
} = {
    [RequestType.ReserveSeat]: {
        availablePriorUserStatus: [UserStatusType.None],
        targetStatus: UserStatusType.Reserved,
        tasks: [TaskType.UpdateSeatStatus, TaskType.StartCurrentTimer, TaskType.UpdateUserStatus],
        requireSeatPosition: "Request",
    },
    [RequestType.CancelReservation]: {
        availablePriorUserStatus: [UserStatusType.Reserved],
        targetStatus: UserStatusType.None,
        tasks: [TaskType.StopCurrentTimer, TaskType.UpdateUserStatus, TaskType.UpdateSeatStatus],
        requireSeatPosition: "Existing Status if not None",
    },
    [RequestType.OccupySeat]: {
        availablePriorUserStatus: [UserStatusType.Reserved],
        targetStatus: UserStatusType.Occupied,
        tasks: [TaskType.StopCurrentTimer, TaskType.StartUsageTimer, TaskType.UpdateUserStatus, TaskType.UpdateSeatStatus],
        requireSeatPosition: "Existing Status if not None",
    },
    [RequestType.StopUsingSeat]: {
        availablePriorUserStatus: [UserStatusType.Occupied, UserStatusType.OnBusiness, UserStatusType.Away],
        targetStatus: UserStatusType.None,
        tasks: [TaskType.StopCurrentTimer, TaskType.StopUsageTimer, TaskType.UpdateUserStatus, TaskType.UpdateSeatStatus],
        requireSeatPosition: "Existing Status if not None",
    },
    [RequestType.DoBusiness]: {
        availablePriorUserStatus: [UserStatusType.Occupied],
        targetStatus: UserStatusType.OnBusiness,
        tasks: [TaskType.StartCurrentTimer, TaskType.UpdateUserStatus, TaskType.UpdateSeatStatus],
        requireSeatPosition: "Existing Status if not None",
    },
    [RequestType.FinishBusiness]: {
        availablePriorUserStatus: [UserStatusType.OnBusiness],
        targetStatus: UserStatusType.Occupied,
        tasks: [TaskType.StopCurrentTimer, TaskType.UpdateUserStatus],
        requireSeatPosition: undefined,
    },
    [RequestType.ShiftToBusiness]: {
        availablePriorUserStatus: [UserStatusType.Away],
        targetStatus: UserStatusType.OnBusiness,
        tasks: [TaskType.StopCurrentTimer, TaskType.StartCurrentTimer, TaskType.UpdateUserStatus],
        requireSeatPosition: undefined,
    },
    [RequestType.LeaveAway]: {
        availablePriorUserStatus: [UserStatusType.Occupied],
        targetStatus: UserStatusType.Away,
        tasks: [TaskType.StartCurrentTimer, TaskType.UpdateUserStatus, TaskType.UpdateSeatStatus],
        requireSeatPosition: "Existing Status if not None",
    },
    [RequestType.ResumeUsing]: {
        availablePriorUserStatus: [UserStatusType.Away],
        targetStatus: UserStatusType.Occupied,
        tasks: [TaskType.StopCurrentTimer, TaskType.UpdateUserStatus, TaskType.UpdateSeatStatus],
        requireSeatPosition: "Existing Status if not None",
    },
    [RequestType.ChangeCurrentTimeoutTime]: {
        availablePriorUserStatus: [UserStatusType.Reserved, UserStatusType.Away, UserStatusType.OnBusiness],
        targetStatus: "Existing Status",
        tasks: [TaskType.StopCurrentTimer, TaskType.StartCurrentTimer, TaskType.UpdateUserStatus],
        requireSeatPosition: undefined,
    },
    [RequestType.ChangeUsageTimeoutTime]: {
        availablePriorUserStatus: [UserStatusType.Occupied],
        targetStatus: "Existing Status",
        tasks: [TaskType.StopUsageTimer, TaskType.StartUsageTimer, TaskType.UpdateUserStatus],
        requireSeatPosition: undefined,
    },
    [RequestType.Block]: {
        availablePriorUserStatus: [UserStatusType.None, UserStatusType.Reserved, UserStatusType.Occupied, UserStatusType.Away, UserStatusType.OnBusiness],
        targetStatus: UserStatusType.Blocked,
        tasks: [TaskType.StopCurrentTimer, TaskType.StopUsageTimer, TaskType.StartCurrentTimer, TaskType.UpdateUserStatus, TaskType.UpdateSeatStatus],
        requireSeatPosition: "Existing Status if not None",
    },
    [RequestType.Unblock]: {
        availablePriorUserStatus: [UserStatusType.Blocked],
        targetStatus: UserStatusType.None,
        tasks: [TaskType.StopCurrentTimer, TaskType.UpdateUserStatus],
        requireSeatPosition: undefined,
    },
    [RequestType.NoOp]: {
        availablePriorUserStatus: [UserStatusType.None, UserStatusType.Reserved, UserStatusType.Occupied, UserStatusType.OnBusiness, UserStatusType.Away, UserStatusType.Blocked],
        targetStatus: "Existing Status",
        tasks: [],
        requireSeatPosition: undefined,
    },
};
