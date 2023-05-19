import UserStatusHandler from "../handler/UserStatusHandler";
import * as functions from "firebase-functions";

export const reserveSeatHandler = (
    data: any,
    context: functions.https.CallableContext
): Promise<boolean> => {
    return UserStatusHandler.reserveSeat(
        "87qDBiucwAaEbfV195l1vBTzeMVY",
        "4Hsrozz5rv4QC1N4HNPs",
        "vRmTtQs1RghW7ELm2k4C",
        "PjYgs4phmL0EP4f13qkN",
    ).then((result) => {
        return result.committed;
    });
};

