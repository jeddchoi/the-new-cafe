import {onDocumentWritten} from "firebase-functions/v2/firestore";
import {COLLECTION_GROUP_SECTION_NAME, COLLECTION_GROUP_STORE_NAME} from "../../_firestore/NameConstant";


export const onSectionWritten =
    onDocumentWritten(
        {
            document: `${COLLECTION_GROUP_STORE_NAME}/{storeId}/${COLLECTION_GROUP_SECTION_NAME}/{sectionId}`,
            region: "asia-northeast3",
        },
        (event) => {
            return Promise.resolve();
        }
    );
