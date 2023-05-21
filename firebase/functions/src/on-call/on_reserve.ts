import {CallableRequest} from "firebase-functions/v2/https";
import {UserSeatUpdateRequest} from "../model/UserSeatUpdateRequest";
import {UserStatusType} from "../model/UserStatus";
import {throwFunctionsHttpsError} from "../util/functions_helper";
import UserStatusHandler from "../handler/UserStatusHandler";

export const reserveSeatHandler = (
    request: CallableRequest<UserSeatUpdateRequest>,
): Promise<boolean> => {
    if (request.data.targetStatusType === UserStatusType.Reserved) {
        throwFunctionsHttpsError("invalid-argument", `Wrong status type : ${request.data.targetStatusType}`);
    }
    if (!request.data.seatPosition) {
        throwFunctionsHttpsError("invalid-argument", "Seat position is not provided");
    }
    if (!request.data.until && !request.data.durationInSeconds) {
        throwFunctionsHttpsError("invalid-argument", "Until or durationInSeconds is not provided");
    }
    if (!request.auth) {
        throwFunctionsHttpsError("unauthenticated", "User is not authenticated");
    }

    return UserStatusHandler.reserveSeat(
        "87qDBiucwAaEbfV195l1vBTzeMVY",
        "4Hsrozz5rv4QC1N4HNPs",
        "vRmTtQs1RghW7ELm2k4C",
        "PjYgs4phmL0EP4f13qkN",
    ).then((result) => {
        return result.committed;
    });
};

