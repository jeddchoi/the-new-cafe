import FirestoreUtil from "../util/FirestoreUtil";

export default class StoreHandler {
    static getStoreData(storeId: string) {
        return FirestoreUtil.getStoreDocRef(storeId).get()
            .then((value) => value.data());
    }
}
