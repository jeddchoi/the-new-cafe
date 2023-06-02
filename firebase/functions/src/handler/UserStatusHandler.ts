import {
    CURRENT_TIMER_PROPERTY_NAME,
    ITimerTask,
    IUserStatusExternal,
    USAGE_TIMER_PROPERTY_NAME,
    UserStatus,
} from "../model/UserStatus";
import {TaskType} from "../model/TaskType";
import RealtimeDatabaseUtil from "../util/RealtimeDatabaseUtil";


export default class UserStatusHandler {
    static getUserStatusData(uid: string): Promise<UserStatus> {
        return RealtimeDatabaseUtil.getUserStatus(uid).once("value").then((snapshot) => {
            const val = snapshot.val() as IUserStatusExternal;
            return UserStatus.fromExternal(uid, val);
        });
    }

    static updateUserStatusData(userId: string, updateContent: { [key in keyof IUserStatusExternal]?: IUserStatusExternal[key] }): Promise<void> {
        return RealtimeDatabaseUtil.getUserStatus(userId).update(updateContent);
    }

    static updateUserTimerTask(userId: string, taskType: TaskType.StartCurrentTimer | TaskType.StartUsageTimer, timerTaskInfo: ITimerTask) {
        if (taskType === TaskType.StartCurrentTimer) {
            return RealtimeDatabaseUtil.getUserStatus(userId).child(CURRENT_TIMER_PROPERTY_NAME).update(timerTaskInfo);
        } else {
            return RealtimeDatabaseUtil.getUserStatus(userId).child(USAGE_TIMER_PROPERTY_NAME).update(timerTaskInfo);
        }
    }

    static removeUserTimerTask(userId: string, taskType: TaskType.StopCurrentTimer | TaskType.StopUsageTimer) {
        if (taskType === TaskType.StopCurrentTimer) {
            return RealtimeDatabaseUtil.getUserStatus(userId).child(CURRENT_TIMER_PROPERTY_NAME).remove();
        } else {
            return RealtimeDatabaseUtil.getUserStatus(userId).child(USAGE_TIMER_PROPERTY_NAME).remove();
        }
    }
}
