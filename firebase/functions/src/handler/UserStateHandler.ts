import {
    CURRENT_TIMER_PROPERTY_NAME,
    IUserStateExternal,
    TimerInfo,
    USAGE_TIMER_PROPERTY_NAME,
    UserState,
} from "../model/UserState";
import {TaskType} from "../model/TaskType";
import RealtimeDatabaseUtil from "../util/RealtimeDatabaseUtil";


export default class UserStateHandler {
    static getUserStateData(uid: string): Promise<UserState> {
        return RealtimeDatabaseUtil.getUserState(uid).once("value").then((snapshot) => {
            const val = snapshot.val() as IUserStateExternal;
            return UserState.fromExternal(uid, val);
        });
    }

    static updateUserStateData(userId: string, updateContent: { [key in keyof IUserStateExternal]?: IUserStateExternal[key] }): Promise<void> {
        return RealtimeDatabaseUtil.getUserState(userId).update(updateContent);
    }

    static updateUserTimerTask(userId: string, taskType: TaskType.StartCurrentTimer | TaskType.StartUsageTimer, timerTaskInfo: TimerInfo) {
        if (taskType === TaskType.StartCurrentTimer) {
            return RealtimeDatabaseUtil.getUserState(userId).child(CURRENT_TIMER_PROPERTY_NAME).update(timerTaskInfo);
        } else {
            return RealtimeDatabaseUtil.getUserState(userId).child(USAGE_TIMER_PROPERTY_NAME).update(timerTaskInfo);
        }
    }

    static removeUserTimerTask(userId: string, taskType: TaskType.StopCurrentTimer | TaskType.StopUsageTimer) {
        if (taskType === TaskType.StopCurrentTimer) {
            return RealtimeDatabaseUtil.getUserState(userId).child(CURRENT_TIMER_PROPERTY_NAME).remove();
        } else {
            return RealtimeDatabaseUtil.getUserState(userId).child(USAGE_TIMER_PROPERTY_NAME).remove();
        }
    }
}
