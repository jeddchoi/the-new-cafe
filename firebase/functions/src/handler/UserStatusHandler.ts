import RealtimeDatabaseUtil, {TransactionResult} from "../util/RealtimeDatabaseUtil";
import {IUserStatusExternal, UserStatusChangeReason, UserStatusType} from "../model/UserStatus";

export default class UserStatusHandler {
    static reserveSeat(userId: string, storeId:string, sectionId: string, seatId: string): Promise<TransactionResult> {
        return RealtimeDatabaseUtil.updateUserStatusData(userId, (existing) => {
            const existingStatus = existing as IUserStatusExternal | undefined;
            if (existingStatus && existingStatus.status !== UserStatusType.None) {
                return;
            }
            return {
                lastStatus: UserStatusType.None,
                status: UserStatusType.Reserved,
                statusUpdatedAt: Date.now(),
                statusUpdatedBy: UserStatusChangeReason.UserAction,
                seatPosition: {
                    storeId,
                    sectionId,
                    seatId,
                },
            };
        });
    }
}
