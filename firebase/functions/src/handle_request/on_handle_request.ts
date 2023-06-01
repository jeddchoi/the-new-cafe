import {logger} from "firebase-functions/v2";
import {ITimerTask} from "../model/UserStatus";
import {Seat, SeatStatusType} from "../model/Seat";
import {RequestTypeInfo} from "../model/RequestTypeInfo";
import {StatusInfo} from "../model/StatusInfo";
import {TaskType} from "../model/TaskType";
import {RequestType} from "../model/RequestType";
import {UserStatusType} from "../model/UserStatusType";
import {UserStatusChangeReason} from "../model/UserStatusChangeReason";
import {throwFunctionsHttpsError} from "../util/functions_helper";
import CloudTasksUtil from "../util/CloudTasksUtil";
import RealtimeDatabaseUtil from "../util/RealtimeDatabaseUtil";
import {MyRequest} from "../model/MyRequest";
import FirestoreUtil from "../util/FirestoreUtil";


export async function requestHandler(
    request: MyRequest
): Promise<void> {
    if (request.requestType === RequestType.NoOp) {
        logger.debug("Don't do anything");
        return Promise.resolve();
    }
    logger.debug(`Handling request ... ${JSON.stringify(request)}`);
    const requestInfo = RequestTypeInfo[request.requestType];
    logger.debug(`[Request Info] ${JSON.stringify(requestInfo)}`);

    const existingUserStatus = await RealtimeDatabaseUtil.getUserStatusData(request.userId);
    logger.debug(`[Exising Status] ${JSON.stringify(existingUserStatus)}`);

    if (!requestInfo.availablePriorStatus.includes(existingUserStatus.status)) {
        throwFunctionsHttpsError("failed-precondition", `${RequestType[request.requestType]} Request can't be accepted when existing user status is ${UserStatusType[existingUserStatus.status]}`);
    }
    if (requestInfo.requireSeatPosition === "Request" && request.seatPosition === undefined) {
        throwFunctionsHttpsError("invalid-argument", `${RequestType[request.requestType]} Request should be provided with seat position`);
    }
    if (requestInfo.requireSeatPosition === "Existing Status if not None" && existingUserStatus.status !== UserStatusType.None && existingUserStatus.seatPosition === undefined) {
        throwFunctionsHttpsError("failed-precondition", `${RequestType[request.requestType]} Request can't be accepted when existing user seat position doesn't exist and status is not None`);
    }

    let usedSeat: Seat | undefined;
    if (existingUserStatus.seatPosition) {
        usedSeat = await FirestoreUtil.getSeatData(existingUserStatus.seatPosition);
        logger.debug(`[Used Seat] ${JSON.stringify(usedSeat)}`);
        if (usedSeat?.currentUserId !== request.userId) {
            throwFunctionsHttpsError("failed-precondition", `Something is corrupted. UserId(${request.userId}) of request is not same as current user id(${usedSeat?.currentUserId}) of used seat`);
        }
    }

    const timer = new CloudTasksUtil();
    let promise = Promise.resolve();
    requestInfo.tasks.forEach((taskType) => {
        switch (taskType) {
            case TaskType.StopCurrentTimer:
                promise = promise.then(() => {
                    if (existingUserStatus.currentTimer) {
                        if (requestInfo.isTimeout) {
                            logger.debug(`[Task #${TaskType[taskType]}] Remove current timer`);
                            return RealtimeDatabaseUtil.removeUserTimerTask(request.userId, "currentTimer");
                        } else {
                            logger.debug(`[Task #${TaskType[taskType]}] Cancel current timer & Remove current timer`);
                            return timer.cancelTimer(existingUserStatus.currentTimer?.timerTaskName).then(() => {
                                return RealtimeDatabaseUtil.removeUserTimerTask(request.userId, "currentTimer");
                            });
                        }
                    } else {
                        logger.debug(`[Task #${TaskType[taskType]}] No current timer`);
                        return Promise.resolve();
                    }
                });
                break;
            case TaskType.StopUsageTimer:
                promise = promise.then(() => {
                    if (existingUserStatus.usageTimer) {
                        if (requestInfo.isTimeout) {
                            logger.debug(`[Task #${TaskType[taskType]}] Remove usage timer`);
                            return RealtimeDatabaseUtil.removeUserTimerTask(request.userId, "usageTimer");
                        } else {
                            logger.debug(`[Task #${TaskType[taskType]}] Cancel usage timer & Remove usage timer`);
                            return timer.cancelTimer(existingUserStatus.usageTimer?.timerTaskName).then(() => {
                                return RealtimeDatabaseUtil.removeUserTimerTask(request.userId, "usageTimer");
                            });
                        }
                    } else {
                        logger.debug(`[Task #${TaskType[taskType]}] No usage timer`);
                        return Promise.resolve();
                    }
                });
                break;
            case TaskType.StartCurrentTimer:
            case TaskType.StartUsageTimer:
                promise = promise.then(() => {
                    if (request.deadlineInfo !== undefined) {
                        let timeoutRequest: MyRequest;
                        if (requestInfo.targetStatus !== "Existing Status") {
                            logger.debug(`[Task #${TaskType[taskType]}] Start new timer : targetStatus is not Existing Status`);
                            timeoutRequest = MyRequest.newInstance(
                                StatusInfo[requestInfo.targetStatus].requestTypeIfTimeout,
                                request.userId,
                                UserStatusChangeReason.Timeout,
                                request.deadlineInfo?.keepStatusUntil,
                                request.seatPosition,
                                StatusInfo[requestInfo.targetStatus].defaultTimeoutAfterInSeconds
                            );
                        } else {
                            logger.debug(`[Task #${TaskType[taskType]}] Start new timer : targetStatus is Existing Status`);
                            timeoutRequest = MyRequest.newInstance(
                                StatusInfo[existingUserStatus.status].requestTypeIfTimeout,
                                request.userId,
                                UserStatusChangeReason.UserAction,
                                request.startStatusAt,
                                request.seatPosition,
                                request.deadlineInfo?.keepStatusUntil,
                            );
                        }

                        return timer.startTimer(timeoutRequest).then((task) => {
                            let timer: "currentTimer" | "usageTimer";
                            if (taskType === TaskType.StartCurrentTimer) {
                                timer = "currentTimer";
                            } else {
                                timer = "usageTimer";
                            }
                            return RealtimeDatabaseUtil.updateUserTimerTask(request.userId, timer, <ITimerTask>{
                                timerTaskName: task.name,
                                startStatusAt: timeoutRequest.startStatusAt,
                                keepStatusUntil: timeoutRequest.deadlineInfo?.keepStatusUntil,
                            });
                        });
                    } else {
                        return Promise.resolve();
                    }
                });
                break;
            case TaskType.UpdateUserStatus:
                promise = promise.then(() => {
                    logger.debug(`[Task #${TaskType[taskType]}] Update user status`);
                    return RealtimeDatabaseUtil.updateUserStatusData(request.userId, {
                        lastStatus: existingUserStatus.status,
                        status: requestInfo.targetStatus === "Existing Status" ? existingUserStatus.status : requestInfo.targetStatus,
                        statusUpdatedAt: request.startStatusAt,
                        statusUpdatedBy: request.reason,
                        seatPosition: requestInfo.requireSeatPosition === "Request" ? request.seatPosition : (requestInfo.targetStatus === UserStatusType.None ? null : existingUserStatus.seatPosition),
                    }).then();
                });
                break;
            case TaskType.UpdateSeatStatus:
                promise = promise.then(() => {
                    if (request.seatPosition && requestInfo.targetStatus !== "Existing Status") {
                        logger.debug(`[Task #${TaskType[taskType]}] Update seat status`);
                        const targetSeatStatus = StatusInfo[requestInfo.targetStatus].seatStatus;
                        return FirestoreUtil.updateSeat(request.seatPosition,
                            {
                                currentUserId: (targetSeatStatus === SeatStatusType.None) ? null : request.userId,
                                status: targetSeatStatus,
                                isAvailable: targetSeatStatus === SeatStatusType.None,
                            });
                    } else {
                        logger.debug(`[Task #${TaskType[taskType]}] Don't update seat status`);
                        return Promise.resolve();
                    }
                });
                break;
        }
    });

    return promise;
}
