import {logger} from "firebase-functions/v2";
import RealtimeDatabaseUtil from "../util/RealtimeDatabaseUtil";
import {
    ISeatPosition,
    ITimerTask,
    IUserStatusExternal,
    UserStatusChangeReason,
    UserStatusType,
} from "../model/UserStatus";
import {isEqual} from "lodash";
import {UserActionRequest} from "../model/request/UserActionRequest";

class UserStatusHandler {
    static updateUserStatus(
        userActionRequest: UserActionRequest,
        predicate: (existingUser: IUserStatusExternal) => boolean,
        setNullToSeatPosition: boolean,
        currentTimerTaskName: string | undefined | null,
        usageTimerTaskName: string | undefined | null,
    ) {
        return RealtimeDatabaseUtil.updateUserStatusData(userActionRequest.userId, (existing) => {
            if (!existing) { // handle null or undefined
                logger.debug(`return ${existing}`);
                return existing;
            }
            if (!isEqual(existing.seatPosition, userActionRequest.seatPosition)) {
                logger.debug("SeatPosition is not same as one of request.");
                return; // not committed
            }
            if (!predicate(existing)) {
                logger.debug("Existing user status is not ready");
                return; // not committed
            }
            let currentTimer;
            if (currentTimerTaskName === null) {
                currentTimer = null;
            } else if (currentTimerTaskName === undefined) {
                currentTimer = existing.currentTimer;
            } else {
                currentTimer = <ITimerTask>{
                    timerTaskName: currentTimerTaskName,
                    keepStatusUntil: userActionRequest.deadlineInfo?.keepStatusUntil,
                    startStatusAt: userActionRequest.startStatusAt,
                };
            }

            let usageTimer;
            if (usageTimerTaskName === null) {
                usageTimer = null;
            } else if (usageTimerTaskName === undefined) {
                usageTimer = existing.usageTimer;
            } else {
                usageTimer = <ITimerTask>{
                    timerTaskName: usageTimerTaskName,
                    keepStatusUntil: userActionRequest.deadlineInfo?.keepStatusUntil,
                    startStatusAt: userActionRequest.startStatusAt,
                };
            }

            return <IUserStatusExternal>{
                lastStatus: existing.status,
                status: userActionRequest.targetStatusType,
                statusUpdatedAt: userActionRequest.startStatusAt,
                statusUpdatedBy: userActionRequest.reason,
                seatPosition: setNullToSeatPosition ? null : userActionRequest.seatPosition,
                usageTimer,
                currentTimer,
            };
        }).then((result) => result.committed);
    }


    static cancelReservation(userId: string, seatPosition: ISeatPosition, startStatusAt: number, statusUpdatedBy: UserStatusChangeReason): Promise<boolean> {
        logger.debug(`[UserStatusHandler] Cancel Reservation / ${JSON.stringify(seatPosition)} (${new Date(startStatusAt).toISOString()} ~ ) / ${statusUpdatedBy.toString()}`);
        return RealtimeDatabaseUtil.updateUserStatusData(userId, (existing) => {
            logger.debug(`HERE = ${JSON.stringify(existing)}`);
            if (!existing) {
                logger.debug("return null");
                return null;
            }

            if (existing.status !== UserStatusType.Reserved) {
                logger.debug(`UserStatusType(${existing.status}) of existing user status is not ${UserStatusType[UserStatusType.Reserved]}`);
                return;
            }

            if (!isEqual(existing.seatPosition, seatPosition)) {
                logger.debug(`Seat position(${seatPosition.storeId}/${seatPosition.sectionId}/${seatPosition.seatId}) of existing user status is not same as one(${existing.seatPosition?.storeId}/${existing.seatPosition?.sectionId}/${existing.seatPosition?.seatId}) of request`);
                return;
            }
            return <IUserStatusExternal>{
                lastStatus: existing.status,
                status: UserStatusType.None,
                statusUpdatedAt: startStatusAt,
                statusUpdatedBy: statusUpdatedBy,
                seatPosition: null,
                currentTimer: null,
                usageTimer: null,
            };
        }).then((result) => result.committed);
    }

    static occupySeat(userId: string, seatPosition: ISeatPosition, startStatusAt: number, keepStatusUntil: number, timerTaskName: string): Promise<boolean> {
        logger.debug(`[UserStatusHandler] Occupy Seat / ${JSON.stringify(seatPosition)} (${new Date(startStatusAt).toISOString()} ~ ${new Date(keepStatusUntil).toISOString()}) / ${timerTaskName}`);
        return RealtimeDatabaseUtil.updateUserStatusData(userId, (existing) => {
            logger.debug(`HERE = ${JSON.stringify(existing)}`);
            if (!existing) {
                logger.debug("return null");
                return null;
            }
            if (existing.status !== UserStatusType.Reserved) {
                logger.debug(`UserStatusType(${existing.status}) of existing user status is not ${UserStatusType[UserStatusType.Reserved]}`);
                return;
            }
            if (!isEqual(existing.seatPosition, seatPosition)) {
                logger.debug(`Seat position(${seatPosition.storeId}/${seatPosition.sectionId}/${seatPosition.seatId}) of existing user status is not same as one(${existing.seatPosition?.storeId}/${existing.seatPosition?.sectionId}/${existing.seatPosition?.seatId}) of request`);
                return;
            }

            return <IUserStatusExternal>{
                lastStatus: existing.status,
                status: UserStatusType.Occupied,
                statusUpdatedAt: startStatusAt,
                statusUpdatedBy: UserStatusChangeReason.UserAction,
                seatPosition: seatPosition,
                currentTimer: null,
                usageTimer: <ITimerTask>{
                    timerTaskName: timerTaskName,
                    startStatusAt,
                    keepStatusUntil,
                },
            };
        }).then((result) => result.committed);
    }


    static stopUsingSeat(userId: string, seatPosition: ISeatPosition, startStatusAt: number, statusUpdatedBy: UserStatusChangeReason): Promise<boolean> {
        logger.debug(`[UserStatusHandler] Stop Using Seat / ${JSON.stringify(seatPosition)} (${new Date(startStatusAt).toISOString()} ~ ) / ${statusUpdatedBy.toString()}`);
        return RealtimeDatabaseUtil.updateUserStatusData(userId, (existing) => {
            logger.debug(`HERE = ${JSON.stringify(existing)}`);
            if (!existing) {
                logger.debug("return null");
                return null;
            }
            const availableStatuses = [UserStatusType.Occupied, UserStatusType.Vacant, UserStatusType.OnTask];
            if (!availableStatuses.includes(existing.status)) {
                logger.debug(`UserStatusType(${existing.status}) of existing user status doesn't belong to ${availableStatuses.map((value) => UserStatusType[value]).join(", ")}`);
                return;
            }
            if (!isEqual(existing.seatPosition, seatPosition)) {
                logger.debug(`Seat position(${seatPosition.storeId}/${seatPosition.sectionId}/${seatPosition.seatId}) of existing user status is not same as one(${existing.seatPosition?.storeId}/${existing.seatPosition?.sectionId}/${existing.seatPosition?.seatId}) of request`);
                return;
            }
            return <IUserStatusExternal>{
                lastStatus: existing.status,
                status: UserStatusType.None,
                statusUpdatedAt: startStatusAt,
                statusUpdatedBy: statusUpdatedBy,
                seatPosition: null,
                currentTimer: null,
                usageTimer: null,
            };
        }).then((result) => result.committed);
    }

    static goVacant(userId: string, seatPosition: ISeatPosition, startStatusAt: number, keepStatusUntil: number, timerTaskName: string, statusUpdatedBy: UserStatusChangeReason): Promise<boolean> {
        logger.debug(`[UserStatusHandler] Go Vacant / ${JSON.stringify(seatPosition)} (${new Date(startStatusAt).toISOString()} ~ ${new Date(keepStatusUntil).toISOString()}) / ${timerTaskName} / ${statusUpdatedBy.toString()}`);
        return RealtimeDatabaseUtil.updateUserStatusData(userId, (existing) => {
            logger.debug(`HERE = ${JSON.stringify(existing)}`);
            if (!existing) {
                logger.debug("return null");
                return null;
            }
            const availableStatuses = [UserStatusType.Occupied, UserStatusType.OnTask];
            if (!availableStatuses.includes(existing.status)) {
                logger.debug(`UserStatusType(${existing.status}) of existing user status doesn't belong to ${availableStatuses.map((value) => UserStatusType[value]).join(", ")}`);
                return;
            }
            if (!isEqual(existing.seatPosition, seatPosition)) {
                logger.debug(`Seat position(${seatPosition.storeId}/${seatPosition.sectionId}/${seatPosition.seatId}) of existing user status is not same as one(${existing.seatPosition?.storeId}/${existing.seatPosition?.sectionId}/${existing.seatPosition?.seatId}) of request`);
                return;
            }
            return <IUserStatusExternal>{
                lastStatus: existing.status,
                status: UserStatusType.Vacant,
                statusUpdatedAt: startStatusAt,
                statusUpdatedBy: statusUpdatedBy,
                seatPosition: seatPosition,
                currentTimer: <ITimerTask>{
                    timerTaskName: timerTaskName,
                    startStatusAt,
                    keepStatusUntil,
                },
                usageTimer: existing.usageTimer,
            };
        }).then((result) => result.committed);
    }

    static onTask(userId: string, seatPosition: ISeatPosition, startStatusAt: number, keepStatusUntil: number, timerTaskName: string): Promise<boolean> {
        logger.debug(`[UserStatusHandler] On Task / ${JSON.stringify(seatPosition)} (${new Date(startStatusAt).toISOString()} ~ ${new Date(keepStatusUntil).toISOString()}) / ${timerTaskName}`);
        return RealtimeDatabaseUtil.updateUserStatusData(userId, (existing) => {
            logger.debug(`HERE = ${JSON.stringify(existing)}`);
            if (!existing) {
                logger.debug("return null");
                return null;
            }
            const availableStatuses = [UserStatusType.Occupied, UserStatusType.Vacant];
            if (!availableStatuses.includes(existing.status)) {
                logger.debug(`UserStatusType(${existing.status}) of existing user status doesn't belong to ${availableStatuses.map((value) => UserStatusType[value]).join(", ")}`);
                return;
            }
            if (!isEqual(existing.seatPosition, seatPosition)) {
                logger.debug(`Seat position(${seatPosition.storeId}/${seatPosition.sectionId}/${seatPosition.seatId}) of existing user status is not same as one(${existing.seatPosition?.storeId}/${existing.seatPosition?.sectionId}/${existing.seatPosition?.seatId}) of request`);
                return;
            }
            return <IUserStatusExternal>{
                lastStatus: existing.status,
                status: UserStatusType.OnTask,
                statusUpdatedAt: startStatusAt,
                statusUpdatedBy: UserStatusChangeReason.UserAction,
                seatPosition: seatPosition,
                currentTimer: <ITimerTask>{
                    timerTaskName: timerTaskName,
                    startStatusAt,
                    keepStatusUntil,
                },
                usageTimer: existing.usageTimer,
            };
        }).then((result) => result.committed);
    }

    static returnToSeat(userId: string, seatPosition: ISeatPosition, startStatusAt: number, statusUpdatedBy: UserStatusChangeReason): Promise<boolean> {
        logger.debug(`[UserStatusHandler] Return to Seat / ${JSON.stringify(seatPosition)} (${new Date(startStatusAt).toISOString()} ~ ) / ${statusUpdatedBy.toString()}`);
        return RealtimeDatabaseUtil.updateUserStatusData(userId, (existing) => {
            logger.debug(`HERE = ${JSON.stringify(existing)}`);
            if (!existing) {
                logger.debug("return null");
                return null;
            }
            const availableStatuses = [UserStatusType.Vacant, UserStatusType.OnTask];
            if (!availableStatuses.includes(existing.status)) {
                logger.debug(`UserStatusType(${existing.status}) of existing user status doesn't belong to ${availableStatuses.map((value) => UserStatusType[value]).join(", ")}`);
                return;
            }
            if (!isEqual(existing.seatPosition, seatPosition)) {
                logger.debug(`Seat position(${seatPosition.storeId}/${seatPosition.sectionId}/${seatPosition.seatId}) of existing user status is not same as one(${existing.seatPosition?.storeId}/${existing.seatPosition?.sectionId}/${existing.seatPosition?.seatId}) of request`);
                return;
            }
            return <IUserStatusExternal>{
                lastStatus: existing.status,
                status: UserStatusType.Occupied,
                statusUpdatedAt: startStatusAt,
                statusUpdatedBy: statusUpdatedBy,
                seatPosition: seatPosition,
                currentTimer: null,
                usageTimer: existing.usageTimer,
            };
        }).then((result) => result.committed);
    }

    static blockUser(userId: string, startStatusAt: number, keepStatusUntil: number, timerTaskName: string): Promise<boolean> {
        logger.debug(`[UserStatusHandler] Block User / (${new Date(startStatusAt).toISOString()} ~ ${new Date(keepStatusUntil).toISOString()}) / ${timerTaskName}`);
        return RealtimeDatabaseUtil.updateUserStatusData(userId, (existing) => {
            logger.debug(`HERE = ${JSON.stringify(existing)}`);
            if (!existing) {
                logger.debug("return null");
                return null;
            }
            if (existing.status === UserStatusType.Blocked) {
                logger.debug(`UserStatusType(${existing.status}) of existing user status is ${UserStatusType[UserStatusType.Blocked]}`);
                return;
            }
            return <IUserStatusExternal>{
                lastStatus: existing.status,
                status: UserStatusType.Blocked,
                statusUpdatedAt: startStatusAt,
                statusUpdatedBy: UserStatusChangeReason.Admin,
                seatPosition: null,
                currentTimer: <ITimerTask>{
                    timerTaskName: timerTaskName,
                    startStatusAt,
                    keepStatusUntil,
                },
                usageTimer: null,
            };
        }).then((result) => result.committed);
    }

    static unblockUser(userId: string, seatPosition: ISeatPosition, startStatusAt: number, statusUpdatedBy: UserStatusChangeReason): Promise<boolean> {
        logger.debug(`[UserStatusHandler] Unblock User / ${JSON.stringify(seatPosition)} (${new Date(startStatusAt).toISOString()} ~ ) / ${statusUpdatedBy.toString()}`);
        return RealtimeDatabaseUtil.updateUserStatusData(userId, (existing) => {
            logger.debug(`HERE = ${JSON.stringify(existing)}`);
            if (!existing) {
                logger.debug("return null");
                return null;
            }
            if (existing.status !== UserStatusType.Blocked) {
                logger.debug(`UserStatusType(${existing.status}) of existing user status is NOT ${UserStatusType[UserStatusType.Blocked]}`);
                return;
            }
            return <IUserStatusExternal>{
                lastStatus: existing.status,
                status: UserStatusType.None,
                statusUpdatedAt: startStatusAt,
                statusUpdatedBy: statusUpdatedBy,
                seatPosition: null,
                currentTimer: null,
                usageTimer: null,
            };
        }).then((result) => result.committed);
    }
}

export default UserStatusHandler;

