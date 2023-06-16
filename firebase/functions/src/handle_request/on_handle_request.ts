import {RequestType} from "../model/RequestType";
import SeatHandler from "../handler/SeatHandler";
import UserStateHandler from "../handler/UserStateHandler";
import {IUserStateExternal, SeatPosition} from "../model/UserState";
import {UserStateType} from "../model/UserStateType";
import {TransactionResult} from "@firebase/database-types";
import {https, logger} from "firebase-functions/v2";

async function reserveSeat(userId: string, seatPosition: SeatPosition | null, current: number, endTime: number | null) {
    if (!seatPosition) throw new https.HttpsError("invalid-argument", "seatPosition is required");
    const isReserved = await SeatHandler.reserveSeat(userId, seatPosition, endTime);
    if (!isReserved) throw new https.HttpsError("failed-precondition", "Failed to reserve seat");

    return UserStateHandler.reserveSeat(userId, seatPosition, current, endTime);
}

function occupySeat(userId: string, current: number, endTime: number | null) {
    return UserStateHandler.occupySeat(userId, current, endTime).then(transformToSeatPositionIfSuccess).then((seatPosition) => {
        return SeatHandler.occupySeat(userId, seatPosition, endTime);
    });
}

function quit(userId: string) {
    return UserStateHandler.getUserStateData(userId).then((userState) => {
        const seatPosition = userState.status?.overall.seatPosition;
        const ps = [];
        if (seatPosition) {
            ps.push(SeatHandler.freeSeat(userId, seatPosition));
        }
        ps.push(UserStateHandler.quit(userId));
        return Promise.all(ps);
    });
}

function resumeUsing(userId: string) {
    return UserStateHandler.getUserStateData(userId).then((userState) => {
        const ps = [];
        if (userState.status?.overall?.seatPosition) {
            ps.push(SeatHandler.resumeUsing(userId, userState.status?.overall?.seatPosition));
        }
        ps.push(UserStateHandler.removeTemporaryState(userId));
        return Promise.all(ps);
    });
}

function goTemporary(userId: string, targetState: UserStateType.Away | UserStateType.OnBusiness, current: number, endTime: number | null) {
    return UserStateHandler.updateUserTemporaryStateInSession(userId, targetState, current, endTime)
        .then(transformToSeatPositionIfSuccess).then((seatPosition) => {
            return SeatHandler.away(userId, seatPosition);
        });
}

function changeOverallTimeoutTime(userId: string, endTime: number | null) {
    return UserStateHandler.getUserStateData(userId).then((userState) => {
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
    });
}

function changeTemporaryTimeoutTime(userId: string, endTime: number | null) {
    return UserStateHandler.updateTemporaryTimer(userId, endTime);
}

export async function requestHandler(
    userId: string,
    requestType: RequestType,
    seatPosition: SeatPosition | null,
    endTime: number | null,
    current: number = new Date().getTime(),
) {
    logger.info(`[${current} -> ${endTime}] request: ${requestType} ${JSON.stringify(seatPosition)} by ${userId}`);

    switch (requestType) {
        case RequestType.ReserveSeat: {
            await reserveSeat(userId, seatPosition, current, endTime);
            break;
        }
        case RequestType.OccupySeat: {
            await occupySeat(userId, current, endTime);
            break;
        }
        case RequestType.Quit: {
            await quit(userId);
            break;
        }
        case RequestType.ResumeUsing: {
            await resumeUsing(userId);
            break;
        }
        case RequestType.LeaveAway: {
            await goTemporary(userId, UserStateType.Away, current, endTime);
            break;
        }
        case RequestType.DoBusiness:
        case RequestType.ShiftToBusiness: {
            await goTemporary(userId, UserStateType.OnBusiness, current, endTime);
            break;
        }
        case RequestType.ChangeOverallTimeoutTime: {
            await changeOverallTimeoutTime(userId, endTime);
            break;
        }
        case RequestType.ChangeTemporaryTimeoutTime: {
            await changeTemporaryTimeoutTime(userId, endTime);
            break;
        }
    }
}


function transformToSeatPositionIfSuccess(result: TransactionResult) {
    if (!result.committed) throw new https.HttpsError("failed-precondition", "Failed to update User State");
    const userState = result.snapshot.val() as IUserStateExternal;
    if (!userState.status?.overall.seatPosition) {
        throw new https.HttpsError("invalid-argument", "seatPosition of existing state is required");
    }
    return userState.status?.overall.seatPosition;
}
