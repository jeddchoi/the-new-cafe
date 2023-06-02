import {logger} from "firebase-functions/v2";
import {ITimerTask} from "../model/UserStatus";
import {Seat, SeatStatusType} from "../model/Seat";
import {RequestTypeInfo} from "../model/RequestTypeInfo";
import {StatusInfo} from "../model/StatusInfo";
import {TaskType} from "../model/TaskType";
import {RequestType} from "../model/RequestType";
import {UserStatusType} from "../model/UserStatusType";
import {UserStatusChangeReason} from "../model/UserStatusChangeReason";
import {MyRequest} from "../model/MyRequest";
import {throwFunctionsHttpsError} from "../util/functions_helper";
import CloudTasksUtil from "../util/CloudTasksUtil";
import UserStatusHandler from "../handler/UserStatusHandler";
import SeatHandler from "../handler/SeatHandler";


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

    const existingUserStatus = await UserStatusHandler.getUserStatusData(request.userId);
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
        usedSeat = await SeatHandler.getSeatData(existingUserStatus.seatPosition);
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
            case TaskType.StopUsageTimer:
                promise = promise.then(() => {
                    let ret = Promise.resolve();
                    const timerTaskNameToStop = taskType === TaskType.StopCurrentTimer ?
                        existingUserStatus.currentTimer?.timerTaskName : existingUserStatus.usageTimer?.timerTaskName;
                    if (!timerTaskNameToStop) {
                        logger.debug(`[Task #${TaskType[taskType]}] No timer to stop`);
                        return ret;
                    }
                    if (!requestInfo.isTimeoutRequest) {
                        logger.debug(`[Task #${TaskType[taskType]}] Cancel timer & Remove timer info of user status`);
                        ret = ret.then(() => timer.cancelTimer(timerTaskNameToStop));
                    }
                    logger.debug(`[Task #${TaskType[taskType]}] Remove timer info of user status`);
                    ret = ret.then(() => UserStatusHandler.removeUserTimerTask(request.userId, taskType));
                    return ret;
                });
                break;
            case TaskType.StartCurrentTimer:
            case TaskType.StartUsageTimer:
                promise = promise.then(() => {
                    if (request.deadlineInfo !== undefined) {
                        let timeoutRequest: MyRequest;
                        if (requestInfo.targetStatus === "Existing Status") {
                            logger.debug(`[Task #${TaskType[taskType]}] Change existing timer with different deadline`);
                            timeoutRequest = MyRequest.newInstance(
                                StatusInfo[existingUserStatus.status].requestTypeIfTimeout,
                                request.userId,
                                UserStatusChangeReason.UserAction,
                                request.startStatusAt,
                                request.seatPosition,
                                request.deadlineInfo?.keepStatusUntil,
                            );
                        } else {
                            logger.debug(`[Task #${TaskType[taskType]}] Start new timer : targetStatus is not Existing Status`);
                            timeoutRequest = MyRequest.newInstance(
                                StatusInfo[requestInfo.targetStatus].requestTypeIfTimeout,
                                request.userId,
                                UserStatusChangeReason.Timeout,
                                request.deadlineInfo?.keepStatusUntil,
                                request.seatPosition,
                                StatusInfo[requestInfo.targetStatus].defaultTimeoutAfterInSeconds
                            );
                        }

                        return timer.startTimer(timeoutRequest).then((task) => {
                            return UserStatusHandler.updateUserTimerTask(request.userId, taskType, <ITimerTask>{
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
                    return UserStatusHandler.updateUserStatusData(request.userId, {
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
                        return SeatHandler.updateSeat(request.seatPosition,
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
