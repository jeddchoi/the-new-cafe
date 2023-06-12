import {ISectionExternal} from "../model/Section";
import FirestoreUtil from "../util/FirestoreUtil";


export default class SectionHandler {
    static getSectionData(storeId: string, sectionId: string) {
        return FirestoreUtil.getSectionDocRef(storeId, sectionId).get()
            .then((value) => value.data());
    }
}
