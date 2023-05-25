import {logger} from "firebase-functions/v2";
import {defineInt} from "firebase-functions/params";
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
        }).then((result) => {
            return result.committed;
        });
    }
}

export default UserStatusHandler;

