import {onDocumentWritten} from "firebase-functions/v2/firestore";
import {
    COLLECTION_GROUP_SEAT_NAME,
    COLLECTION_GROUP_SECTION_NAME,
    COLLECTION_GROUP_STORE_NAME,
} from "../../_firestore/NameConstant";


export const onSeatWritten =
    onDocumentWritten(
        {
            document: `${COLLECTION_GROUP_STORE_NAME}/{storeId}/${COLLECTION_GROUP_SECTION_NAME}/{sectionId}/${COLLECTION_GROUP_SEAT_NAME}/{seatId}`,
            region: "asia-northeast3",
        },
        (event) => {
            return Promise.resolve();
        }
    );
