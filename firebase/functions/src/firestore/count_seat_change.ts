import * as functions from "firebase-functions";
import {ISeatExternal} from "../model/Seat";
import {FieldValue} from "firebase-admin/firestore";
import {throwFunctionsHttpsError} from "../util/functions_helper";
import {Change} from "firebase-functions/lib/common/change";
import {DocumentSnapshot} from "firebase-functions/lib/v1/providers/firestore";


export const countSeatChangeHandler = async (
    change: Change<DocumentSnapshot>,
    // eslint-disable-next-line @typescript-eslint/no-unused-vars
    context: functions.EventContext<{ storeId: string, sectionId: string, seatId: string }>,
) => {
    const beforeSeat = change.before.data() as ISeatExternal | null;
    const afterSeat = change.after.data() as ISeatExternal | null;
    const sectionRef = change.after.ref.parent.parent ?? throwFunctionsHttpsError("not-found", "section not found");
    let totalSeatsInc: number;
    let availableSeatsInc: number;
    if (change.after.exists && !change.before.exists) { // 없다가 생기면
        totalSeatsInc = 1;
        if (afterSeat?.isAvailable === true) {
            availableSeatsInc = 1;
        } else {
            availableSeatsInc = 0;
        }
    } else if (!change.after.exists && change.before.exists) { // 있다가 없으면
        totalSeatsInc = -1;
        if (beforeSeat?.isAvailable === true) {
            availableSeatsInc = -1;
        } else {
            availableSeatsInc = 0;
        }
    } else {
        totalSeatsInc = 0;
        if (beforeSeat?.isAvailable === false && afterSeat?.isAvailable === true) {
            availableSeatsInc = 1;
        } else if (beforeSeat?.isAvailable === true && afterSeat?.isAvailable === false) {
            availableSeatsInc = -1;
        } else {
            availableSeatsInc = 0;
        }
    }
    if (totalSeatsInc === 0 && availableSeatsInc === 0) return null;

    await sectionRef.update({
        totalSeats: FieldValue.increment(totalSeatsInc),
        totalAvailableSeats: FieldValue.increment(availableSeatsInc),
    });
    functions.logger.info(`[Seat ${change.after.id}] Counter updated.: ${availableSeatsInc}/${totalSeatsInc}`);
    return null;
};
