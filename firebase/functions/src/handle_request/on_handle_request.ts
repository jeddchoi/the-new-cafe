import {logger} from "firebase-functions/v2";
import {ITimerTask, IUserStatusExternal, StatusInfo, UserStatusChangeReason, UserStatusType} from "../model/UserStatus";
import {throwFunctionsHttpsError} from "../util/functions_helper";
import CloudTasksUtil from "../util/CloudTasksUtil";
import RealtimeDatabaseUtil from "../util/RealtimeDatabaseUtil";
import {MyRequest, RequestType, RequestTypeInfo, TaskType} from "../model/MyRequest";
import FirestoreUtil from "../util/FirestoreUtil";
import {Seat, SeatStatusType} from "../model/Seat";


export async function requestHandler(
    request: MyRequest
): Promise<void> {
    if (request.requestType === RequestType.NoOp) {
        logger.debug("Don't do anything");
        return Promise.resolve();
    }

    const requestInfo = RequestTypeInfo[request.requestType];
    const existingUserStatus = await RealtimeDatabaseUtil.getUserStatusData(request.userId);
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
    }
    if (existingUserStatus.seatPosition !== undefined && usedSeat?.currentUserId !== request.userId) {
        throwFunctionsHttpsError("failed-precondition", `Something is corrupted. UserId(${request.userId}) of request is not same as current user id(${usedSeat?.currentUserId}) of used seat`);
    }

    const timer = new CloudTasksUtil();
    let promise = Promise.resolve();
    requestInfo.tasks.forEach((taskType) => {
        switch (taskType) {
            case TaskType.StopCurrentTimer:
                promise = promise.then(() => {
                    if (existingUserStatus.currentTimer) {
                        return timer.cancelTimer(existingUserStatus.currentTimer?.timerTaskName);
                    } else {
                        return Promise.resolve();
                    }
                });
                break;
            case TaskType.StopUsageTimer:
                promise = promise.then(() => {
                    if (existingUserStatus.usageTimer) {
                        return timer.cancelTimer(existingUserStatus.usageTimer?.timerTaskName);
                    } else {
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
                            timeoutRequest = MyRequest.newInstance(
                                StatusInfo[requestInfo.targetStatus].requestTypeIfTimeout,
                                request.userId,
                                UserStatusChangeReason.Timeout,
                                request.deadlineInfo?.keepStatusUntil,
                                request.seatPosition,
                                StatusInfo[requestInfo.targetStatus].defaultTimeoutAfterInSeconds
                            );
                        } else {
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
                    return RealtimeDatabaseUtil.updateUserStatusData(request.userId, (existing) => {
                        if (!existing) {
                            return existing;
                        }
                        return <IUserStatusExternal>{
                            lastStatus: existing.status,
                            status: requestInfo.targetStatus,
                            statusUpdatedAt: request.startStatusAt,
                            statusUpdatedBy: request.reason,
                            seatPosition: requestInfo.requireSeatPosition === "Request" ? request.seatPosition : (requestInfo.targetStatus === UserStatusType.None ? undefined : existing.seatPosition),
                            usageTimer: existing.usageTimer,
                            currentTimer: existing.currentTimer,
                        };
                    }).then();
                });
                break;
            case TaskType.UpdateSeatStatus:
                promise = promise.then(() => {
                    if (request.seatPosition && requestInfo.targetStatus !== "Existing Status") {
                        const targetSeatStatus = StatusInfo[requestInfo.targetStatus].seatStatus;
                        return FirestoreUtil.updateSeat(
                            request.seatPosition,
                            (targetSeatStatus === SeatStatusType.None) ? undefined : request.userId,
                            targetSeatStatus,
                            targetSeatStatus === SeatStatusType.None,
                        );
                    } else {
                        return Promise.resolve();
                    }
                });
                break;
        }
    });
}
//
// function _onHandleUserActionRequest(
//     request: UserActionRequest,
//     predicateToRequest: ((userActionRequest: UserActionRequest) => boolean),
//     predicateToExistingUserStatus: ((existingStatus: IUserStatusExternal) => boolean),
//     stopCurrentTimer: boolean,
//     stopUsageTimer: boolean,
//     startCurrentTimer: TimeoutRequest | undefined | null,
//     startUsageTimer: TimeoutRequest | undefined | null,
//     updateUserStatus: boolean,
//     updateSeatStatus: boolean,
// ): Promise<boolean> {
//     logger.info("================= On Handle Request ==================", {request: request});
//
//     // Validate request
//     if (predicateToRequest(request)) {
//         throwFunctionsHttpsError("invalid-argument", `Something is wrong : ${request.toString()}`);
//     }
//
//     const timer = new CloudTasksUtil();
//     const promises: Promise<boolean>[] = [];
//
//
//     promises.push(RealtimeDatabaseUtil.getUserStatusData(request.userId).then((userStatus) => {
//         const stops = [];
//         if (stopCurrentTimer && userStatus.currentTimer) {
//             stops.push(timer.cancelTimer(userStatus.currentTimer.timerTaskName));
//         }
//         if (stopUsageTimer && userStatus.usageTimer) {
//             stops.push(timer.cancelTimer(userStatus.usageTimer.timerTaskName));
//         }
//         return Promise.all(stops).then((results) => results.every((result) => result));
//     }).then(async (result) => {
//         if (!result) {
//             throwFunctionsHttpsError("internal", "Stopping timer failed.");
//         }
//         if (startCurrentTimer) {
//             return await timer.startTimer(startCurrentTimer);
//         }
//         if (startUsageTimer) {
//             return await timer.startTimer(startUsageTimer);
//         }
//         return; // no need timer
//     }).then(async (timerTask) => {
//         if (updateUserStatus) {
//             return await UserStatusHandler.updateUserStatus(request,
//                 predicateToExistingUserStatus,
//                 request.targetStatusType === UserStatusType.None,
//                 startCurrentTimer ? timerTask?.name : startCurrentTimer,
//                 startUsageTimer ? timerTask?.name : startUsageTimer,
//             );
//         }
//         return true;
//     }));
//
//     const keepStatusUntil = request.deadlineInfo?.keepStatusUntil;
//
//     // 1. Handle seat status change
//     promises.push(SeatStatusHandler.reserveSeat(
//         request.userId,
//         request.seatPosition,
//     ));
//
//     // 2. Start timer and handle user status change
//     promises.push(timer.startTimer(
//         TimeoutRequest.newInstance(
//             request.userId,
//             keepStatusUntil,
//             UserStatusType.None,
//             request.seatPosition,
//             "timeoutOnReserve",
//             undefined,
//             undefined,
//         ),
//     ).then((task) => {
//         if (!task.name) {
//             throwFunctionsHttpsError("internal", "Timer task failed to start");
//         }
//         return UserStatusHandler.reserveSeat(
//             request.userId,
//             request.seatPosition,
//             request.startStatusAt,
//             keepStatusUntil,
//             task.name
//         );
//     }));
//
//     // 3. TODO: handle user history update
//
//     return Promise.all(promises).then((results) => {
//         return results.every((result) => result);
//     });
// }
