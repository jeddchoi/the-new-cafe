import {Section, sectionConverter} from "../model/Section";
import FirestoreUtil from "../util/FirestoreUtil";


export default class SectionHandler {
    static getSectionData(storeId: string, sectionId: string): Promise<Section | undefined> {
        return FirestoreUtil.getSectionDocRef(storeId, sectionId).get()
            .then((value) => value.data());
    }
}
