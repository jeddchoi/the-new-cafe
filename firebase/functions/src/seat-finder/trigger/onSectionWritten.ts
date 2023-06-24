import {onDocumentWritten} from "firebase-functions/v2/firestore";
import {
    COLLECTION_GROUP_SECTION_NAME,
    COLLECTION_GROUP_STORE_NAME,
    DOCUMENT_TOTAL_AVAILABLE_SEATS_NAME,
    DOCUMENT_TOTAL_SEATS_NAME,
    DOCUMENT_TOTAL_SECTIONS_NAME,
} from "../../_firestore/NameConstant";
import {FieldValue} from "firebase-admin/firestore";
import {logger} from "firebase-functions/v2";
import {Section} from "../../_firestore/model/Section";


export const onSectionWritten =
    onDocumentWritten(
        {
            document: `${COLLECTION_GROUP_STORE_NAME}/{storeId}/${COLLECTION_GROUP_SECTION_NAME}/{sectionId}`,
            region: "asia-northeast3",
        },
        (event) => {
            logger.debug(`[onSectionWritten] called ${JSON.stringify(event.data)}`);
            let totalSectionsDiff = 0;
            let totalSeatsDiff = 0;
            let availableSeatsDiff = 0;

            const before = event.data?.before.data() as Section | null;
            const after = event.data?.after.data() as Section | null;

            if (before && after) { // update
                totalSeatsDiff = after.totalSeats - before.totalSeats;
                availableSeatsDiff = after.totalAvailableSeats - before.totalAvailableSeats;
            } else if (!before && after) { // create
                totalSectionsDiff = 1;
                totalSeatsDiff = after.totalSeats;
                availableSeatsDiff = after.totalAvailableSeats;
            } else if (before && !after) { // delete
                totalSectionsDiff = -1;
                totalSeatsDiff = -before.totalSeats;
                availableSeatsDiff = -before.totalAvailableSeats;
            }

            if (totalSectionsDiff !== 0 || totalSeatsDiff !== 0 || availableSeatsDiff !== 0) {
                return event.data?.after.ref.parent.parent?.update(
                    DOCUMENT_TOTAL_SECTIONS_NAME,
                    FieldValue.increment(totalSectionsDiff),
                    DOCUMENT_TOTAL_AVAILABLE_SEATS_NAME,
                    FieldValue.increment(availableSeatsDiff),
                    DOCUMENT_TOTAL_SEATS_NAME,
                    FieldValue.increment(totalSeatsDiff),
                ).then(() => {
                    logger.debug(`[onSectionWritten] Updated availableSeats : ${availableSeatsDiff} / totalSeats : ${totalSeatsDiff} / totalSections: ${totalSectionsDiff}`);
                });
            } else {
                return;
            }
        }
    );
