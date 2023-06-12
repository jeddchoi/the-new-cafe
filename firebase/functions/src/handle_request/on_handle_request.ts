import {UserActionRequest} from "../model/UserActionRequest";
import {RequestType} from "../model/RequestType";
import SeatHandler from "../handler/SeatHandler";
import {throwFunctionsHttpsError} from "../util/functions_helper";
import UserStateHandler from "../handler/UserStateHandler";
import {IUserStateExternal} from "../model/UserState";
import {SeatStateType} from "../model/Seat";
import {deserializeSeatId} from "../model/SeatPosition";
import {UserStateType} from "../model/UserStateType";
import {TransactionResult} from "@firebase/database-types";
import {logger} from "firebase-functions/v2";


function transformToSeatPosition(result: TransactionResult) {
    if (!result.committed) throwFunctionsHttpsError("failed-precondition", "Failed to update User State");
    const userState = result.snapshot.val() as IUserStateExternal;
    if (!userState.status?.overall.seatPosition) {
        throwFunctionsHttpsError("invalid-argument", "seatPosition of existing state is required");
    }
    return deserializeSeatId(userState.status?.overall.seatPosition);
}

export async function requestHandler(
    userId: string,
    request: UserActionRequest,
) {
    const current = new Date().getTime();
    const endTime = request.getEndTime(current);
    logger.info(`[${current} -> ${endTime}] request: ${JSON.stringify(request)}`);

    switch (request.requestType) {
        // after reserve seat, update user state
        case RequestType.ReserveSeat: {
            if (!request.seatPosition) throwFunctionsHttpsError("invalid-argument", "seatPosition is required");
            const isReserved = await SeatHandler.reserveSeat(userId, request.seatPosition, endTime);
            if (!isReserved) throwFunctionsHttpsError("failed-precondition", "Failed to reserve seat");
            return UserStateHandler.reserveSeat(userId, request.seatPosition, current, endTime);
        }
        // update user overall state, update seat state
        case RequestType.OccupySeat: {
            return UserStateHandler.occupySeat(userId, current, endTime).then(transformToSeatPosition).then((seatPosition) => {
                return SeatHandler.occupySeat(userId, seatPosition, endTime);
            });
        }
        // remove user state status  -> trigger -> update seat state
        case RequestType.CancelReservation:
        case RequestType.StopUsingSeat: {
            return UserStateHandler.quit(userId);
        }
        // remove user temporary state -> trigger -> update seat state
        case RequestType.FinishBusiness:
        case RequestType.ResumeUsing: {
            return UserStateHandler.removeTemporaryState(userId);
        }
        // update user temporary state, update seat state
        case RequestType.DoBusiness:
        case RequestType.LeaveAway:
        case RequestType.ShiftToBusiness: {
            const targetState = request.requestType === RequestType.LeaveAway ? UserStateType.Away : UserStateType.OnBusiness;
            return UserStateHandler.updateUserTemporaryStateInSession(userId, targetState, current, endTime, targetState === UserStateType.Away)
                .then(transformToSeatPosition).then((seatPosition) => {
                    return SeatHandler.away(userId, seatPosition);
                });
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
}
