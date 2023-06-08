import {logger} from "firebase-functions/v2";
import {ITimerTask, SeatId} from "../model/UserState";
import {Seat, SeatStateType} from "../model/Seat";
import {RequestTypeInfo} from "../model/RequestTypeInfo";
import {StateInfo} from "../model/StateInfo";
import {TaskType} from "../model/TaskType";
import {RequestType} from "../model/RequestType";
import {UserStateType} from "../model/UserStateType";
import {UserStateChangeReason} from "../model/UserStateChangeReason";
import {MyRequest} from "../model/MyRequest";
import {throwFunctionsHttpsError} from "../util/functions_helper";
import CloudTasksUtil from "../util/CloudTasksUtil";
import UserStateHandler from "../handler/UserStateHandler";
import SeatHandler from "../handler/SeatHandler";
import {RequestCondition} from "../model/RequestCondition";


export async function requestHandler(
    request: MyRequest,
    isTimeout: boolean,
): Promise<void> {
    if (request.requestType === RequestType.NoOp) {
        logger.debug("Don't do anything");
        return Promise.resolve();
    }
    logger.debug(`Handling request ... [isTimeout = ${isTimeout}] ${JSON.stringify(request)}`);
    const requestInfo = RequestTypeInfo[request.requestType];
    logger.debug(`[Request Info] ${JSON.stringify(requestInfo)}`);

    const existingUserState = await UserStateHandler.getUserStateData(request.userId);
    logger.debug(`[Exising State] ${JSON.stringify(existingUserState)}`);

    // Check if existing user state is valid
    if (!requestInfo.availablePriorUserState.includes(existingUserState.state)) {
        throwFunctionsHttpsError("failed-precondition", `${RequestType[request.requestType]} Request can't be accepted when existing user state is ${UserStateType[existingUserState.state]}`);
    }

    const timer = new CloudTasksUtil();
    let promise: Promise<boolean | void> = Promise.resolve();
    requestInfo.tasks.forEach((taskType) => {
        switch (taskType) {
            case TaskType.StopCurrentTimer:
            case TaskType.StopUsageTimer:
                promise = promise.then(() => {
                    let ret = Promise.resolve();
                    const timerTaskNameToStop = taskType === TaskType.StopCurrentTimer ?
                        existingUserState.currentTimer?.timerTaskName : existingUserState.usageTimer?.timerTaskName;
                    if (!timerTaskNameToStop) {
                        logger.debug(`[Task #${TaskType[taskType]}] No timer to stop`);
                        return ret;
                    }
                    if (!isTimeout) {
                        logger.debug(`[Task #${TaskType[taskType]}] Cancel timer & Remove timer info of user state`);
                        ret = ret.then(() => timer.cancelTimer(timerTaskNameToStop));
                    }
                    logger.debug(`[Task #${TaskType[taskType]}] Remove timer info of user state`);
                    ret = ret.then(() => UserStateHandler.removeUserTimerTask(request.userId, taskType));
                    return ret;
                });
                break;
            case TaskType.StartCurrentTimer:
            case TaskType.StartUsageTimer:
                promise = promise.then(() => {
                    if (request.deadlineInfo !== undefined) {
                        let timeoutRequest: MyRequest;

                        if (requestInfo.targetState === "Existing State") {
                            logger.debug(`[Task #${TaskType[taskType]}] Change existing timer with different deadline`);
                            timeoutRequest = MyRequest.newInstance(
                                StateInfo[existingUserState.state].requestTypeIfTimeout,
                                request.userId,
                                UserStateChangeReason.Timeout,
                                request.deadlineInfo?.keepStateUntil,
                                request.seatPosition,
                            );
                        } else {
                            logger.debug(`[Task #${TaskType[taskType]}] Start new timer`);
                            timeoutRequest = MyRequest.newInstance(
                                StateInfo[requestInfo.targetState].requestTypeIfTimeout,
                                request.userId,
                                UserStateChangeReason.Timeout,
                                request.deadlineInfo?.keepStateUntil,
                                request.seatPosition,
                            );
                        }

                        return timer.startTimer(timeoutRequest).then((task) => {
                            return UserStateHandler.updateUserTimerTask(request.userId, taskType, <ITimerTask>{
                                timerTaskName: task.name,
                                startStateAt: request.startStateAt,
                                keepStateUntil: request.deadlineInfo?.keepStateUntil,
                            });
                        });
                    } else { // No deadline
                        return Promise.resolve();
                    }
                });
                break;
            case TaskType.UpdateUserState:
                promise = promise.then(() => {
                    logger.debug(`[Task #${TaskType[taskType]}] Update user state`);
                    return UserStateHandler.updateUserStateData(request.userId, {
                        lastState: existingUserState.state,
                        state: requestInfo.targetState === "Existing State" ? existingUserState.state : requestInfo.targetState,
                        stateUpdatedAt: request.startStateAt,
                        stateUpdatedBy: request.reason,
                        seatPosition: requestInfo.requiredConditions.includes(RequestCondition.ProvidedSeatPositionInRequest) ? request.seatPosition : (requestInfo.targetState === UserStateType.None ? null : existingUserState.seatPosition),
                    }).then();
                });
                break;
            case TaskType.UpdateSeatState:
                promise = promise.then(() => {
                    if (requestInfo.targetState !== "Existing State") {
                        logger.debug(`[Task #${TaskType[taskType]}] Update seat state`);
                        const targetSeatState = StateInfo[requestInfo.targetState].seatState;
                        let seatPosition: SeatId | undefined;
                        let predicate;
                        if (requestInfo.requiredConditions.includes(RequestCondition.ProvidedSeatPositionInRequest)) {
                            seatPosition = request.seatPosition ?? throwFunctionsHttpsError("invalid-argument", `${RequestType[request.requestType]} Request should be provided with seat position`);

                            predicate = (existing: Seat | undefined) => {
                                if (!existing) return false;
                                if (requestInfo.requiredConditions.includes(RequestCondition.RequestSeatIsAvailable)) {
                                    if (!existing.isAvailable || existing.userId || existing.state !== SeatStateType.Empty) {
                                        return false;
                                    }
                                }
                                return true;
                            };
                        }
                        if (requestInfo.requiredConditions.includes(RequestCondition.SeatPositionInExistingUserState)) {
                            seatPosition = existingUserState.seatPosition ?? throwFunctionsHttpsError("failed-precondition", `${RequestType[request.requestType]} Request can't be accepted when existing user seat position doesn't exist`);

                            predicate = (existing: Seat | undefined) => {
                                if (!existing) return false;
                                if (requestInfo.requiredConditions.includes(RequestCondition.SeatOfExistingUserStateIsOccupiedByMe)) {
                                    if (existing.userId !== request.userId || existing.isAvailable) {
                                        return false;
                                    }
                                }
                                return true;
                            };
                        }
                        if (seatPosition && predicate) {
                            return SeatHandler.transaction(seatPosition, predicate, (existing) => {
                                return {
                                    userId: (targetSeatState === SeatStateType.Empty) ? null : request.userId,
                                    state: targetSeatState,
                                    isAvailable: targetSeatState === SeatStateType.Empty,
                                };
                            });
                        }
                    }
                    logger.debug(`[Task #${TaskType[taskType]}] Don't update seat state`);
                    return Promise.resolve(true);
                });
                break;
        }
    });

    return promise.then();
}
