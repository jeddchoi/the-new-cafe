import {logger} from "firebase-functions";
import {defineInt} from "firebase-functions/params";
import RealtimeDatabaseUtil, {TransactionResult} from "../util/RealtimeDatabaseUtil";
import {ITimerTask, IUserStatusExternal, UserStatusChangeReason, UserStatusType} from "../model/UserStatus";

const timeReserveDurationInSeconds = defineInt("TIME_RESERVE_DURATION_IN_SECONDS");

export default class UserStatusHandler {
    static reserveSeat(userId: string, storeId:string, sectionId: string, seatId: string): Promise<TransactionResult> {
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
                seatPosition: {
                    storeId,
                    sectionId,
                    seatId,
                },
                currentTimer: <ITimerTask>{
                    timerTaskName: "",
                    installedAt: installedAt,
                    fireAt: fireAt,
                },
            };
        });
    }
}
