import {CallableRequest} from "firebase-functions/v2/https";
import {UserSeatUpdateRequest} from "../model/UserSeatUpdateRequest";

export function onReserve(request: UserSeatUpdateRequest): Promise<boolean> {
    return Promise.resolve(true);
}

export const cancelReservationHandler = (
    request: CallableRequest<UserSeatUpdateRequest>,
): Promise<boolean> => onReserve(request.data);
