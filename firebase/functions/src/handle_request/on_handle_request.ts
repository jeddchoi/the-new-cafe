import {logger} from "firebase-functions/v2";
import {ISeatPosition, ITimerTask} from "../model/UserState";
import {SeatStateType} from "../model/Seat";
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

    // Check if required seat position is provided
    // TODO: At this time, it is required only when reserving a seat
    if (requestInfo.requiredConditions.includes(RequestCondition.ProvidedSeatPositionInRequest)) {
        if (!request.seatPosition) {
            throwFunctionsHttpsError("invalid-argument", `${RequestType[request.requestType]} Request should be provided with seat position`);
        }
        const targetSeat = await SeatHandler.getSeatData(request.seatPosition) ?? throwFunctionsHttpsError("failed-precondition", `${RequestType[request.requestType]} Request can't be accepted when seat position doesn't exist`);
        logger.debug(`[Target Seat of Request] ${JSON.stringify(targetSeat)}`);

        if (requestInfo.requiredConditions.includes(RequestCondition.RequestSeatIsAvailable)) {
            if (!targetSeat.isAvailable || targetSeat.currentUserId || targetSeat.state !== SeatStateType.Empty) {
                throwFunctionsHttpsError("failed-precondition", `${RequestType[request.requestType]} Request can't be accepted when target seat is not available`);
            }
        }
    }

    // Check if seat position of existing user state is provided when required
    if (requestInfo.requiredConditions.includes(RequestCondition.SeatPositionInExistingUserState)) {
        if (!existingUserState.seatPosition) {
            throwFunctionsHttpsError("failed-precondition", `${RequestType[request.requestType]} Request can't be accepted when existing user seat position doesn't exist`);
        }
        const targetSeat = await SeatHandler.getSeatData(existingUserState.seatPosition) ?? throwFunctionsHttpsError("failed-precondition", `${RequestType[request.requestType]} Request can't be accepted when seat position doesn't exist`);
        logger.debug(`[Target Seat of Existing State] ${JSON.stringify(targetSeat)}`);
        if (requestInfo.requiredConditions.includes(RequestCondition.SeatOfExistingUserStateIsOccupiedByMe)) {
            if (targetSeat.currentUserId !== request.userId || targetSeat.isAvailable) {
                throwFunctionsHttpsError("failed-precondition", `Something is corrupted. UserId(${request.userId}) of request is not same as current user id(${targetSeat.currentUserId}) of used seat`);
            }
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
                        let requiredSeatPosition: ISeatPosition | undefined;
                        if (requestInfo.requiredConditions.includes(RequestCondition.ProvidedSeatPositionInRequest) && request.seatPosition) {
                            requiredSeatPosition = request.seatPosition;
                        }
                        if (requestInfo.requiredConditions.includes(RequestCondition.SeatPositionInExistingUserState) && existingUserState.seatPosition) {
                            requiredSeatPosition = existingUserState.seatPosition;
                        }
                        if (!requiredSeatPosition) {
                            throwFunctionsHttpsError("failed-precondition", `${RequestType[request.requestType]} Request should be provided with seat position or need seat position of existing user`);
                        }

                        return SeatHandler.updateSeat(requiredSeatPosition,
                            {
                                currentUserId: (targetSeatState === SeatStateType.Empty) ? null : request.userId,
                                state: targetSeatState,
                                isAvailable: targetSeatState === SeatStateType.Empty,
                            });
                    } else {
                        logger.debug(`[Task #${TaskType[taskType]}] Don't update seat state`);
                        return Promise.resolve();
                    }
                });
                break;
        }
    });

    return promise;
}
