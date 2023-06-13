import {RequestType} from "../model/RequestType";
import SeatHandler from "../handler/SeatHandler";
import {throwFunctionsHttpsError} from "../util/functions_helper";
import UserStateHandler from "../handler/UserStateHandler";
import {IUserStateExternal, SeatPosition} from "../model/UserState";
import {deserializeSeatId} from "../model/SeatPosition";
import {UserStateType} from "../model/UserStateType";
import {TransactionResult} from "@firebase/database-types";
import {logger} from "firebase-functions/v2";


async function reserveSeat(userId: string, seatPosition: SeatPosition | null, current: number, endTime: number | null) {
    if (!seatPosition) throwFunctionsHttpsError("invalid-argument", "seatPosition is required");
    const isReserved = await SeatHandler.reserveSeat(userId, seatPosition, endTime);
    if (!isReserved) throwFunctionsHttpsError("failed-precondition", "Failed to reserve seat");
    return UserStateHandler.reserveSeat(userId, seatPosition, current, endTime);
}

export async function requestHandler(
    userId: string,
    requestType: RequestType,
    seatPosition: SeatPosition | null,
    endTime: number | null,
    current: number = new Date().getTime(),
) {
    const promises = [];
    logger.info(`[${current} -> ${endTime}] request: ${requestType} ${seatPosition} by ${userId}`);
    switch (requestType) {
        // after reserve seat, update user state
        case RequestType.ReserveSeat: {
            promises.push(reserveSeat(userId, seatPosition, current, endTime));
            break;
        }
        // update user overall state, update seat state
        case RequestType.OccupySeat: {
            promises.push(UserStateHandler.occupySeat(userId, current, endTime).then(transformToSeatPosition).then((seatPosition) => {
                return SeatHandler.occupySeat(userId, seatPosition, endTime);
            }));
            break;
        }
        // remove user state status, update seat state
        case RequestType.CancelReservation:
        case RequestType.StopUsingSeat: {
            promises.push(UserStateHandler.getUserStateData(userId).then((userState) => {
                if (userState.status?.overall.seatPosition) {
                    return SeatHandler.freeSeat(userId, deserializeSeatId(userState.status?.overall.seatPosition));
                } else return;
            }));
            promises.push(UserStateHandler.quit(userId));
            break;
        }
        // remove user temporary state, update seat state
        case RequestType.ResumeUsing: {
            promises.push(UserStateHandler.getUserStateData(userId).then((userState) => {
                if (userState.status?.overall?.seatPosition) {
                    return SeatHandler.resumeUsing(userId, deserializeSeatId(userState.status?.overall?.seatPosition));
                } else return;
            }));
            promises.push(UserStateHandler.removeTemporaryState(userId));
            break;
        }
        // update user temporary state, update seat state
        case RequestType.DoBusiness:
        case RequestType.LeaveAway:
        case RequestType.ShiftToBusiness: {
            const targetState = requestType === RequestType.LeaveAway ? UserStateType.Away : UserStateType.OnBusiness;
            promises.push(UserStateHandler.updateUserTemporaryStateInSession(userId, targetState, current, endTime)
                .then(transformToSeatPosition).then((seatPosition) => {
                    return SeatHandler.away(userId, seatPosition);
                }));
            break;
        }
        // update user state temporary timer, update seat state
        case RequestType.ChangeTemporaryTimeoutTime: {
            break;
        }
        // update user state overall timer, update seat state
        case RequestType.ChangeOverallTimeoutTime: {
            break;
        }
    }

    return Promise.all(promises);
}


function transformToSeatPosition(result: TransactionResult) {
    if (!result.committed) throwFunctionsHttpsError("failed-precondition", "Failed to update User State");
    const userState = result.snapshot.val() as IUserStateExternal;
    if (!userState.status?.overall.seatPosition) {
        throwFunctionsHttpsError("invalid-argument", "seatPosition of existing state is required");
    }
    return deserializeSeatId(userState.status?.overall.seatPosition);
}
