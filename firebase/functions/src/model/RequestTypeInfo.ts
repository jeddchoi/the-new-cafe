import {TaskType} from "./TaskType";
import {RequestType} from "./RequestType";
import {UserStateType} from "./UserStateType";
import {RequestCondition} from "./RequestCondition";

export const RequestTypeInfo: {
    [key in RequestType]: {
        availablePriorUserState: UserStateType[],
        targetState: UserStateType | "Existing State",
        tasks: TaskType[],
        requiredConditions: RequestCondition[],
    }
} = {
    [RequestType.ReserveSeat]: {
        availablePriorUserState: [UserStateType.None],
        targetState: UserStateType.Reserved,
        tasks: [TaskType.UpdateSeatState, TaskType.StartCurrentTimer, TaskType.UpdateUserState],
        requiredConditions: [RequestCondition.ProvidedSeatPositionInRequest, RequestCondition.RequestSeatIsAvailable],
    },
    [RequestType.CancelReservation]: {
        availablePriorUserState: [UserStateType.Reserved],
        targetState: UserStateType.None,
        tasks: [TaskType.StopCurrentTimer, TaskType.UpdateUserState, TaskType.UpdateSeatState],
        requiredConditions: [RequestCondition.SeatPositionInExistingUserState, RequestCondition.SeatOfExistingUserStateIsOccupiedByMe],
    },
    [RequestType.OccupySeat]: {
        availablePriorUserState: [UserStateType.Reserved],
        targetState: UserStateType.Occupied,
        tasks: [TaskType.StopCurrentTimer, TaskType.StartUsageTimer, TaskType.UpdateUserState, TaskType.UpdateSeatState],
        requiredConditions: [RequestCondition.SeatPositionInExistingUserState, RequestCondition.SeatOfExistingUserStateIsOccupiedByMe],
    },
    [RequestType.StopUsingSeat]: {
        availablePriorUserState: [UserStateType.Occupied, UserStateType.OnBusiness, UserStateType.Away],
        targetState: UserStateType.None,
        tasks: [TaskType.StopCurrentTimer, TaskType.StopUsageTimer, TaskType.UpdateUserState, TaskType.UpdateSeatState],
        requiredConditions: [RequestCondition.SeatPositionInExistingUserState, RequestCondition.SeatOfExistingUserStateIsOccupiedByMe],
    },
    [RequestType.DoBusiness]: {
        availablePriorUserState: [UserStateType.Occupied],
        targetState: UserStateType.OnBusiness,
        tasks: [TaskType.StartCurrentTimer, TaskType.UpdateUserState, TaskType.UpdateSeatState],
        requiredConditions: [RequestCondition.SeatPositionInExistingUserState, RequestCondition.SeatOfExistingUserStateIsOccupiedByMe],
    },
    [RequestType.FinishBusiness]: {
        availablePriorUserState: [UserStateType.OnBusiness],
        targetState: UserStateType.Occupied,
        tasks: [TaskType.StopCurrentTimer, TaskType.UpdateUserState],
        requiredConditions: [RequestCondition.SeatPositionInExistingUserState, RequestCondition.SeatOfExistingUserStateIsOccupiedByMe],
    },
    [RequestType.ShiftToBusiness]: {
        availablePriorUserState: [UserStateType.Away],
        targetState: UserStateType.OnBusiness,
        tasks: [TaskType.StopCurrentTimer, TaskType.StartCurrentTimer, TaskType.UpdateUserState],
        requiredConditions: [RequestCondition.SeatOfExistingUserStateIsOccupiedByMe],
    },
    [RequestType.LeaveAway]: {
        availablePriorUserState: [UserStateType.Occupied],
        targetState: UserStateType.Away,
        tasks: [TaskType.StartCurrentTimer, TaskType.UpdateUserState, TaskType.UpdateSeatState],
        requiredConditions: [RequestCondition.SeatPositionInExistingUserState, RequestCondition.SeatOfExistingUserStateIsOccupiedByMe],
    },
    [RequestType.ResumeUsing]: {
        availablePriorUserState: [UserStateType.Away],
        targetState: UserStateType.Occupied,
        tasks: [TaskType.StopCurrentTimer, TaskType.UpdateUserState, TaskType.UpdateSeatState],
        requiredConditions: [RequestCondition.SeatPositionInExistingUserState, RequestCondition.SeatOfExistingUserStateIsOccupiedByMe],
    },
    [RequestType.ChangeCurrentTimeoutTime]: {
        availablePriorUserState: [UserStateType.Reserved, UserStateType.Away, UserStateType.OnBusiness],
        targetState: "Existing State",
        tasks: [TaskType.StopCurrentTimer, TaskType.StartCurrentTimer, TaskType.UpdateUserState],
        requiredConditions: [RequestCondition.SeatOfExistingUserStateIsOccupiedByMe],
    },
    [RequestType.ChangeUsageTimeoutTime]: {
        availablePriorUserState: [UserStateType.Occupied],
        targetState: "Existing State",
        tasks: [TaskType.StopUsageTimer, TaskType.StartUsageTimer, TaskType.UpdateUserState],
        requiredConditions: [RequestCondition.SeatOfExistingUserStateIsOccupiedByMe],
    },
    [RequestType.Block]: {
        availablePriorUserState: [UserStateType.None, UserStateType.Reserved, UserStateType.Occupied, UserStateType.Away, UserStateType.OnBusiness],
        targetState: UserStateType.Blocked,
        tasks: [TaskType.StopCurrentTimer, TaskType.StopUsageTimer, TaskType.StartCurrentTimer, TaskType.UpdateUserState, TaskType.UpdateSeatState],
        requiredConditions: [],
    },
    [RequestType.Unblock]: {
        availablePriorUserState: [UserStateType.Blocked],
        targetState: UserStateType.None,
        tasks: [TaskType.StopCurrentTimer, TaskType.UpdateUserState],
        requiredConditions: [],
    },
    [RequestType.NoOp]: {
        availablePriorUserState: [UserStateType.None, UserStateType.Reserved, UserStateType.Occupied, UserStateType.OnBusiness, UserStateType.Away, UserStateType.Blocked],
        targetState: "Existing State",
        tasks: [],
        requiredConditions: [],
    },
};
