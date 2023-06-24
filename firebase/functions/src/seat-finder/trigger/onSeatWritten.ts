import {onDocumentWritten} from "firebase-functions/v2/firestore";
import {
    COLLECTION_GROUP_SEAT_NAME,
    COLLECTION_GROUP_SECTION_NAME,
    COLLECTION_GROUP_STORE_NAME,
    DOCUMENT_TOTAL_AVAILABLE_SEATS_NAME,
    DOCUMENT_TOTAL_SEATS_NAME,
} from "../../_firestore/NameConstant";
import {Seat} from "../../_firestore/model/Seat";
import {FieldValue} from "firebase-admin/firestore";
import {logger} from "firebase-functions/v2";


export const onSeatWritten =
    onDocumentWritten(
        {
            document: `${COLLECTION_GROUP_STORE_NAME}/{storeId}/${COLLECTION_GROUP_SECTION_NAME}/{sectionId}/${COLLECTION_GROUP_SEAT_NAME}/{seatId}`,
            region: "asia-northeast3",
        },
        (event) => {
            logger.debug(`[onSeatWritten] called ${JSON.stringify(event.data)}`);
            let totalSeatsDiff = 0;
            let availableSeatsDiff = 0;

            const before = event.data?.before.data() as Seat | null;
            const after = event.data?.after.data() as Seat | null;

            if (before && after) { // update
                if (before.isAvailable && !after.isAvailable) {
                    availableSeatsDiff = -1;
                } else if (!before.isAvailable && after.isAvailable) {
                    availableSeatsDiff = 1;
                }
            } else if (!before && after) { // create
                totalSeatsDiff = 1;
                if (after.isAvailable) {
                    availableSeatsDiff = 1;
                }
            } else if (before && !after) { // delete
                totalSeatsDiff = -1;
                if (before.isAvailable) {
                    availableSeatsDiff = -1;
                }
            }

            if (totalSeatsDiff !== 0 || availableSeatsDiff !== 0) {
                return event.data?.after.ref.parent.parent?.update(
                    DOCUMENT_TOTAL_AVAILABLE_SEATS_NAME,
                    FieldValue.increment(availableSeatsDiff),
                    DOCUMENT_TOTAL_SEATS_NAME,
                    FieldValue.increment(totalSeatsDiff),
                ).then(() => {
                    logger.debug(`[onSeatWritten] Updated availableSeats : ${availableSeatsDiff} / totalSeats : ${totalSeatsDiff}`);
                });
            } else {
                return;
            }
        }
    );
