import {RequestType} from "../model/RequestType";
import SeatHandler from "../handler/SeatHandler";
import {throwFunctionsHttpsError} from "../util/functions_helper";
import UserStateHandler from "../handler/UserStateHandler";
import {IUserStateExternal, SeatPosition} from "../model/UserState";
import {UserStateType} from "../model/UserStateType";
import {TransactionResult} from "@firebase/database-types";
import {logger} from "firebase-functions/v2";

export async function requestHandler(
    userId: string,
    requestType: RequestType,
    seatPosition: SeatPosition | null,
    endTime: number | null,
    current: number = new Date().getTime(),
) {
    const promises = [];
    logger.info(`[${current} -> ${endTime}] request: ${requestType} ${JSON.stringify(seatPosition)} by ${userId}`);

    switch (requestType) {
        // after reserve seat, update user state
        case RequestType.ReserveSeat: {
            promises.push(reserveSeat(userId, seatPosition, current, endTime));
            break;
        }
        // update user overall state, update seat state
        case RequestType.OccupySeat: {
            promises.push(UserStateHandler.occupySeat(userId, current, endTime).then(transformToSeatPositionIfSuccess).then((seatPosition) => {
                return SeatHandler.occupySeat(userId, seatPosition, endTime);
            }));
            break;
        }
        // remove user state status, update seat state
        case RequestType.CancelReservation:
        case RequestType.StopUsingSeat: {
            promises.push(UserStateHandler.getUserStateData(userId).then((userState) => {
                const seatPosition = userState.status?.overall.seatPosition;
                const ps = [];
                if (seatPosition) {
                    ps.push(SeatHandler.freeSeat(userId, seatPosition));
                }
                ps.push(UserStateHandler.quit(userId));
                return Promise.all(ps);
            }));
            break;
        }
        // remove user temporary state, update seat state
        case RequestType.ResumeUsing: {
            promises.push(UserStateHandler.getUserStateData(userId).then((userState) => {
                const ps = [];
                if (userState.status?.overall?.seatPosition) {
                    ps.push(SeatHandler.resumeUsing(userId, userState.status?.overall?.seatPosition));
                }
                ps.push(UserStateHandler.removeTemporaryState(userId));
                return Promise.all(ps);
            }));
            break;
        }
        // update user temporary state, update seat state
        case RequestType.DoBusiness:
        case RequestType.LeaveAway:
        case RequestType.ShiftToBusiness: {
            const targetState = requestType === RequestType.LeaveAway ? UserStateType.Away : UserStateType.OnBusiness;
            promises.push(UserStateHandler.updateUserTemporaryStateInSession(userId, targetState, current, endTime)
                .then(transformToSeatPositionIfSuccess).then((seatPosition) => {
                    return SeatHandler.away(userId, seatPosition);
                }));
            break;
        }
        // update user state temporary timer, update seat state
        case RequestType.ChangeTemporaryTimeoutTime: {
            promises.push(UserStateHandler.updateTemporaryTimer(userId, endTime));
            break;
        }
        // update user state overall timer, update seat state
        case RequestType.ChangeOverallTimeoutTime: {
            promises.push(UserStateHandler.getUserStateData(userId).then((userState) => {
                const ps = [];
                const state = userState.status?.overall.state;
                const seatPosition = userState.status?.overall.seatPosition;
                if (seatPosition) {
                    if (state === UserStateType.Reserved) {
                        ps.push(SeatHandler.updateReserveEndTime(userId, seatPosition, endTime));
                    } else if (state === UserStateType.Occupied) {
                        ps.push(SeatHandler.updateOccupyEndTime(userId, seatPosition, endTime));
                    }
                }
                ps.push(UserStateHandler.updateOverallTimer(userId, endTime));
                return Promise.all(ps);
            }));
            break;
        }
    }
    return Promise.all(promises);
}

async function reserveSeat(userId: string, seatPosition: SeatPosition | null, current: number, endTime: number | null) {
    if (!seatPosition) throwFunctionsHttpsError("invalid-argument", "seatPosition is required");
    const isReserved = await SeatHandler.reserveSeat(userId, seatPosition, endTime);
    if (!isReserved) throwFunctionsHttpsError("failed-precondition", "Failed to reserve seat");

    return UserStateHandler.reserveSeat(userId, seatPosition, current, endTime);
}

function transformToSeatPositionIfSuccess(result: TransactionResult) {
    if (!result.committed) throwFunctionsHttpsError("failed-precondition", "Failed to update User State");
    const userState = result.snapshot.val() as IUserStateExternal;
    if (!userState.status?.overall.seatPosition) {
        throwFunctionsHttpsError("invalid-argument", "seatPosition of existing state is required");
    }
    return userState.status?.overall.seatPosition;
}
