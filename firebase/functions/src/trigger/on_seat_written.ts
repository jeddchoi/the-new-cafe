import {logger} from "firebase-functions/v2";
import {Change, DocumentSnapshot, FirestoreEvent} from "firebase-functions/v2/firestore";
import {FieldValue} from "firebase-admin/firestore";
import {ISeatExternal} from "../model/Seat";
import {throwFunctionsHttpsError} from "../util/functions_helper";


export const seatWrittenHandler = async (
    event: FirestoreEvent<Change<DocumentSnapshot> | undefined, { storeId: string, sectionId: string, seatId: string }>
) => {
    logger.debug(`seatWrittenHandler ${event.params.storeId} ${event.params.sectionId} ${event.params.seatId}`);
    const promises = [];
    const beforeSeat = event.data?.before?.data() as ISeatExternal | null;
    const afterSeat = event.data?.after?.data() as ISeatExternal | null;
    const sectionRef = event.data?.after?.ref?.parent?.parent ?? throwFunctionsHttpsError("not-found", "section not found");
    let totalSeatsInc: number;
    let availableSeatsInc: number;
    if (event.data?.after?.exists && !event.data?.before?.exists) { // 없다가 생기면
        totalSeatsInc = 1;
        if (afterSeat?.isAvailable === true) {
            availableSeatsInc = 1;
        } else {
            availableSeatsInc = 0;
        }
    } else if (!event.data?.after?.exists && event.data?.before?.exists) { // 있다가 없으면
        totalSeatsInc = -1;
        if (beforeSeat?.isAvailable === true) {
            availableSeatsInc = -1;
        } else {
            availableSeatsInc = 0;
        }
    } else { // update
        totalSeatsInc = 0;
        if (beforeSeat?.isAvailable === false && afterSeat?.isAvailable === true) {
            availableSeatsInc = 1;
        } else if (beforeSeat?.isAvailable === true && afterSeat?.isAvailable === false) {
            availableSeatsInc = -1;
        } else {
            availableSeatsInc = 0;
        }
    }
    if (totalSeatsInc !== 0 || availableSeatsInc !== 0) {
        promises.push(sectionRef.update({
            totalSeats: FieldValue.increment(totalSeatsInc),
            totalAvailableSeats: FieldValue.increment(availableSeatsInc),
        }));
        logger.info(`[Seat ${event.data?.after?.id}] Counter updated.: ${availableSeatsInc}/${totalSeatsInc}`);
    }
    return Promise.all(promises);
};
