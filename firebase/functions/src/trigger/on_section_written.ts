import {https, logger} from "firebase-functions/v2";
import {Change, DocumentSnapshot, FirestoreEvent} from "firebase-functions/v2/firestore";
import {FieldValue} from "firebase-admin/firestore";
import {ISectionExternal} from "../model/Section";


export const sectionWrittenHandler = async (
    event: FirestoreEvent<Change<DocumentSnapshot> | undefined, { storeId: string, sectionId: string }>,
) => {
    logger.debug(`sectionWrittenHandler ${event.params.storeId} ${event.params.sectionId}`);
    const beforeSection = event.data?.before?.data() as ISectionExternal | null;
    const afterSection = event.data?.after?.data() as ISectionExternal | null;
    const storeRef = event.data?.after?.ref?.parent?.parent;
    if (!storeRef) {
        throw new https.HttpsError("not-found", "store not found");
    }

    let totalSeatsInc: number;
    let availableSeatsInc: number;
    let totalSectionsInc: number;

    if (event.data?.after?.exists && !event.data?.before?.exists) { // 없다가 생기면
        totalSeatsInc = afterSection?.totalSeats ?? 0;
        availableSeatsInc = afterSection?.totalAvailableSeats ?? 0;
        totalSectionsInc = 1;
    } else if (!event.data?.after?.exists && event.data?.before?.exists) { // 있다가 없으면
        totalSeatsInc = -(beforeSection?.totalSeats ?? 0);
        availableSeatsInc = -(beforeSection?.totalAvailableSeats ?? 0);
        totalSectionsInc = -1;
    } else {
        totalSeatsInc = (afterSection?.totalSeats ?? 0) - (beforeSection?.totalSeats ?? 0);
        availableSeatsInc = (afterSection?.totalAvailableSeats ?? 0) - (beforeSection?.totalAvailableSeats ?? 0);
        totalSectionsInc = 0;
    }
    if (totalSeatsInc === 0 && availableSeatsInc === 0 && totalSectionsInc === 0) return null;

    await storeRef.update({
        totalSeats: FieldValue.increment(totalSeatsInc),
        totalAvailableSeats: FieldValue.increment(availableSeatsInc),
        totalSections: FieldValue.increment(totalSectionsInc),
    });
    logger.info(`[Section ${event.data?.after?.id}] Counter updated.: ${availableSeatsInc}/${totalSeatsInc}/${totalSectionsInc}`);
    return null;
};
