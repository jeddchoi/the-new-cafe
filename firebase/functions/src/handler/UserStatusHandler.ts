import {logger} from "firebase-functions/v2";
import {defineInt} from "firebase-functions/params";
import RealtimeDatabaseUtil from "../util/RealtimeDatabaseUtil";
import {
    ISeatPosition,
    ITimerTask,
    IUserStatusExternal, UserStatus,
    UserStatusChangeReason,
    UserStatusType,
} from "../model/UserStatus";

class UserStatusHandler {
    static reserveSeat(userId: string, seatPosition: ISeatPosition, requestedAt: number, fireAt: number, timerTaskName: string): Promise<boolean> {
        logger.debug(`reserve seat ${new Date(requestedAt).toISOString()} ${new Date(fireAt).toISOString()} ${timerTaskName}`);
        return RealtimeDatabaseUtil.updateUserStatusData(userId, (existing) => {
            const existingStatus = existing as IUserStatusExternal | undefined;
            if (existingStatus && existingStatus.status !== UserStatusType.None) {
                return;
            }
            return <IUserStatusExternal>{
                lastStatus: UserStatusType.None,
                status: UserStatusType.Reserved,
                statusUpdatedAt: requestedAt,
                statusUpdatedBy: UserStatusChangeReason.UserAction,
                seatPosition: seatPosition,
                currentTimer: <ITimerTask>{
                    timerTaskName: timerTaskName,
                    installedAt: requestedAt,
                    fireAt: fireAt,
                },
            };
        }).then((result) => result.committed);
    }

    static cancelReservation(userId: string, requestedAt: number, statusUpdatedBy: UserStatusChangeReason): Promise<boolean> {
        logger.debug(`cancel reservation ${new Date(requestedAt).toISOString()}`);
        return RealtimeDatabaseUtil.updateUserStatusData(userId, (existing) => {
            const existingStatus = existing as IUserStatusExternal | undefined;
            if (existingStatus && existingStatus.status !== UserStatusType.Reserved) {
                return;
            }
            return <IUserStatusExternal>{
                lastStatus: UserStatusType.Reserved,
                status: UserStatusType.None,
                statusUpdatedAt: requestedAt,
                statusUpdatedBy: statusUpdatedBy,
                seatPosition: null,
                usageTimer: null,
                currentTimer: null,
            };
        }).then((result) => result.committed);
    }
}

export default UserStatusHandler;

