import {Store, storeConverter} from "../model/Store";
import FirestoreUtil from "../util/FirestoreUtil";

export default class StoreHandler {
    static getStoreData(storeId: string): Promise<Store | undefined> {
        return FirestoreUtil.getStoreDocRef(storeId).get()
            .then((value) => value.data());
    }
}
