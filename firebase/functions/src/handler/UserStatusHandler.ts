import {logger} from "firebase-functions/v2";
import RealtimeDatabaseUtil from "../util/RealtimeDatabaseUtil";
import {
    ISeatPosition,
    ITimerTask,
    IUserStatusExternal,
    UserStatusChangeReason,
    UserStatusType,
} from "../model/UserStatus";

class UserStatusHandler {
    static reserveSeat(userId: string, seatPosition: ISeatPosition, requestedAt: number, fireAt: number, timerTaskName: string): Promise<boolean> {
        logger.debug(`[UserStatusHandler] Reserve Seat / ${JSON.stringify(seatPosition)} (${new Date(requestedAt).toISOString()} ~ ${new Date(fireAt).toISOString()}) / ${timerTaskName}`);
        return RealtimeDatabaseUtil.updateUserStatusData(userId, (existing) => {
            const existingStatus = existing as IUserStatusExternal | undefined;
            if (existingStatus && existingStatus.status !== UserStatusType.None) {
                return;
            }
            return <IUserStatusExternal>{
                lastStatus: existingStatus?.status ?? UserStatusType.None,
                status: UserStatusType.Reserved,
                statusUpdatedAt: requestedAt,
                statusUpdatedBy: UserStatusChangeReason.UserAction,
                seatPosition: seatPosition,
                currentTimer: <ITimerTask>{
                    timerTaskName: timerTaskName,
                    installedAt: requestedAt,
                    fireAt: fireAt,
                },
                usageTimer: null,
            };
        }).then((result) => result.committed);
    }

    static cancelReservation(userId: string, seatPosition: ISeatPosition, requestedAt: number, statusUpdatedBy: UserStatusChangeReason): Promise<boolean> {
        logger.debug(`[UserStatusHandler] Cancel Reservation / ${JSON.stringify(seatPosition)} (${new Date(requestedAt).toISOString()} ~ ) / ${statusUpdatedBy.toString()}`);
        return RealtimeDatabaseUtil.updateUserStatusData(userId, (existing) => {
            const existingStatus = existing as IUserStatusExternal | undefined;
            if (!existingStatus) {
                return;
            }
            if (existingStatus.status !== UserStatusType.Reserved) {
                return;
            }
            if (existingStatus.seatPosition !== seatPosition) {
                return;
            }
            return <IUserStatusExternal>{
                lastStatus: existingStatus.status,
                status: UserStatusType.None,
                statusUpdatedAt: requestedAt,
                statusUpdatedBy: statusUpdatedBy,
                seatPosition: null,
                currentTimer: null,
                usageTimer: null,
            };
        }).then((result) => result.committed);
    }

    static occupySeat(userId: string, seatPosition: ISeatPosition, requestedAt: number, fireAt: number, timerTaskName: string): Promise<boolean> {
        logger.debug(`[UserStatusHandler] Occupy Seat / ${JSON.stringify(seatPosition)} (${new Date(requestedAt).toISOString()} ~ ${new Date(fireAt).toISOString()}) / ${timerTaskName}`);
        return RealtimeDatabaseUtil.updateUserStatusData(userId, (existing) => {
            const existingStatus = existing as IUserStatusExternal | undefined;
            if (!existingStatus) {
                return;
            }
            if (existingStatus.status !== UserStatusType.Reserved) {
                return;
            }
            if (existingStatus.seatPosition !== seatPosition) {
                return;
            }

            return <IUserStatusExternal>{
                lastStatus: existingStatus.status,
                status: UserStatusType.Occupied,
                statusUpdatedAt: requestedAt,
                statusUpdatedBy: UserStatusChangeReason.UserAction,
                seatPosition: seatPosition,
                currentTimer: null,
                usageTimer: <ITimerTask>{
                    timerTaskName: timerTaskName,
                    installedAt: requestedAt,
                    fireAt: fireAt,
                },
            };
        }).then((result) => result.committed);
    }


    static stopUsingSeat(userId: string, seatPosition: ISeatPosition, requestedAt: number, statusUpdatedBy: UserStatusChangeReason): Promise<boolean> {
        logger.debug(`[UserStatusHandler] Stop Using Seat / ${JSON.stringify(seatPosition)} (${new Date(requestedAt).toISOString()} ~ ) / ${statusUpdatedBy.toString()}`);
        return RealtimeDatabaseUtil.updateUserStatusData(userId, (existing) => {
            const existingStatus = existing as IUserStatusExternal | undefined;
            if (!existingStatus) {
                return;
            }
            const availableStatuses = [UserStatusType.Occupied, UserStatusType.Vacant, UserStatusType.OnTask];
            if (!availableStatuses.includes(existingStatus.status)) {
                return;
            }
            if (existingStatus.seatPosition !== seatPosition) {
                return;
            }
            return <IUserStatusExternal>{
                lastStatus: existingStatus.status,
                status: UserStatusType.None,
                statusUpdatedAt: requestedAt,
                statusUpdatedBy: statusUpdatedBy,
                seatPosition: null,
                currentTimer: null,
                usageTimer: null,
            };
        }).then((result) => result.committed);
    }

    static goVacant(userId: string, seatPosition: ISeatPosition, requestedAt: number, fireAt: number, timerTaskName: string, statusUpdatedBy: UserStatusChangeReason): Promise<boolean> {
        logger.debug(`[UserStatusHandler] Go Vacant / ${JSON.stringify(seatPosition)} (${new Date(requestedAt).toISOString()} ~ ${new Date(fireAt).toISOString()}) / ${timerTaskName} / ${statusUpdatedBy.toString()}`);
        return RealtimeDatabaseUtil.updateUserStatusData(userId, (existing) => {
            const existingStatus = existing as IUserStatusExternal | undefined;
            if (!existingStatus) {
                return;
            }
            const availableStatuses = [UserStatusType.Occupied, UserStatusType.OnTask];
            if (!availableStatuses.includes(existingStatus.status)) {
                return;
            }
            if (existingStatus.seatPosition !== seatPosition) {
                return;
            }
            return <IUserStatusExternal>{
                lastStatus: existingStatus.status,
                status: UserStatusType.Vacant,
                statusUpdatedAt: requestedAt,
                statusUpdatedBy: statusUpdatedBy,
                seatPosition: seatPosition,
                currentTimer: <ITimerTask>{
                    timerTaskName: timerTaskName,
                    installedAt: requestedAt,
                    fireAt: fireAt,
                },
                usageTimer: existingStatus.usageTimer,
            };
        }).then((result) => result.committed);
    }

    static onTask(userId: string, seatPosition: ISeatPosition, requestedAt: number, fireAt: number, timerTaskName: string): Promise<boolean> {
        logger.debug(`[UserStatusHandler] On Task / ${JSON.stringify(seatPosition)} (${new Date(requestedAt).toISOString()} ~ ${new Date(fireAt).toISOString()}) / ${timerTaskName}`);
        return RealtimeDatabaseUtil.updateUserStatusData(userId, (existing) => {
            const existingStatus = existing as IUserStatusExternal | undefined;
            if (!existingStatus) {
                return;
            }
            const availableStatuses = [UserStatusType.Occupied, UserStatusType.Vacant];
            if (!availableStatuses.includes(existingStatus.status)) {
                return;
            }
            if (existingStatus.seatPosition !== seatPosition) {
                return;
            }
            return <IUserStatusExternal>{
                lastStatus: existingStatus.status,
                status: UserStatusType.OnTask,
                statusUpdatedAt: requestedAt,
                statusUpdatedBy: UserStatusChangeReason.UserAction,
                seatPosition: seatPosition,
                currentTimer: <ITimerTask>{
                    timerTaskName: timerTaskName,
                    installedAt: requestedAt,
                    fireAt: fireAt,
                },
                usageTimer: existingStatus.usageTimer,
            };
        }).then((result) => result.committed);
    }

    static returnToSeat(userId: string, seatPosition: ISeatPosition, requestedAt: number, statusUpdatedBy: UserStatusChangeReason): Promise<boolean> {
        logger.debug(`[UserStatusHandler] Return to Seat / ${JSON.stringify(seatPosition)} (${new Date(requestedAt).toISOString()} ~ ) / ${statusUpdatedBy.toString()}`);
        return RealtimeDatabaseUtil.updateUserStatusData(userId, (existing) => {
            const existingStatus = existing as IUserStatusExternal | undefined;
            if (!existingStatus) {
                return;
            }
            const availableStatuses = [UserStatusType.Vacant, UserStatusType.OnTask];
            if (!availableStatuses.includes(existingStatus.status)) {
                return;
            }
            if (existingStatus.seatPosition !== seatPosition) {
                return;
            }
            return <IUserStatusExternal>{
                lastStatus: existingStatus.status,
                status: UserStatusType.Occupied,
                statusUpdatedAt: requestedAt,
                statusUpdatedBy: statusUpdatedBy,
                seatPosition: seatPosition,
                currentTimer: null,
                usageTimer: existingStatus.usageTimer,
            };
        }).then((result) => result.committed);
    }

    static blockUser(userId: string, requestedAt: number, fireAt: number, timerTaskName: string) : Promise<boolean> {
        logger.debug(`[UserStatusHandler] Block User / (${new Date(requestedAt).toISOString()} ~ ${new Date(fireAt).toISOString()}) / ${timerTaskName}`);
        return RealtimeDatabaseUtil.updateUserStatusData(userId, (existing) => {
            const existingStatus = existing as IUserStatusExternal | undefined;
            if (!existingStatus) {
                return;
            }
            if (existingStatus.status === UserStatusType.Blocked) {
                return;
            }
            return <IUserStatusExternal>{
                lastStatus: existingStatus.status,
                status: UserStatusType.Blocked,
                statusUpdatedAt: requestedAt,
                statusUpdatedBy: UserStatusChangeReason.Admin,
                seatPosition: null,
                currentTimer: <ITimerTask>{
                    timerTaskName: timerTaskName,
                    installedAt: requestedAt,
                    fireAt: fireAt,
                },
                usageTimer: null,
            };
        }).then((result) => result.committed);
    }

    static unblockUser(userId: string, seatPosition: ISeatPosition, requestedAt: number, statusUpdatedBy: UserStatusChangeReason): Promise<boolean> {
        logger.debug(`[UserStatusHandler] Unblock User / ${JSON.stringify(seatPosition)} (${new Date(requestedAt).toISOString()} ~ ) / ${statusUpdatedBy.toString()}`);
        return RealtimeDatabaseUtil.updateUserStatusData(userId, (existing) => {
            const existingStatus = existing as IUserStatusExternal | undefined;
            if (!existingStatus) {
                return;
            }
            if (existingStatus.status === UserStatusType.Blocked) {
                return;
            }
            return <IUserStatusExternal>{
                lastStatus: existingStatus.status,
                status: UserStatusType.None,
                statusUpdatedAt: requestedAt,
                statusUpdatedBy: statusUpdatedBy,
                seatPosition: null,
                currentTimer: null,
                usageTimer: null,
            };
        }).then((result) => result.committed);
    }
}

export default UserStatusHandler;

