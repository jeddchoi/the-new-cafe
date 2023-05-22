import {logger} from "firebase-functions";
import {defineInt} from "firebase-functions/params";
import RealtimeDatabaseUtil from "../util/RealtimeDatabaseUtil";
import {
    ISeatPosition,
    ITimerTask,
    IUserStatusExternal,
    UserStatusChangeReason,
    UserStatusType,
} from "../model/UserStatus";
import {StatusHandler} from "./StatusHandler";


const timeReserveDurationInSeconds = defineInt("TIME_RESERVE_DURATION_IN_SECONDS");

const UserStatusHandler: StatusHandler = class StatusHandler {
    static reserveSeat(userId: string, seatPosition: ISeatPosition): Promise<boolean> {
        const installedAt = Date.now();
        const fireAt = installedAt + timeReserveDurationInSeconds.value() * 1000;

        logger.debug(`reserve seat ${new Date(installedAt).toISOString()} ${new Date(fireAt).toISOString()}`);
        return RealtimeDatabaseUtil.updateUserStatusData(userId, (existing) => {
            const existingStatus = existing as IUserStatusExternal | undefined;
            if (existingStatus && existingStatus.status !== UserStatusType.None) {
                return;
            }
            return <IUserStatusExternal>{
                lastStatus: UserStatusType.None,
                status: UserStatusType.Reserved,
                statusUpdatedAt: installedAt,
                statusUpdatedBy: UserStatusChangeReason.UserAction,
                seatPosition: seatPosition,
                currentTimer: <ITimerTask>{
                    timerTaskName: "",
                    installedAt: installedAt,
                    fireAt: fireAt,
                },
            };
        }).then((result) => {
            return result.committed;
        });
    }
};

export default UserStatusHandler;

