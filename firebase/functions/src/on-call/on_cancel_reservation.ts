import {CallableRequest} from "firebase-functions/v2/https";
import {UserSeatUpdateRequest} from "../model/UserSeatUpdateRequest";


export const cancelReservationHandler = (
    request: CallableRequest<UserSeatUpdateRequest>,
): Promise<boolean> => {
    return Promise.resolve(true);
};
