import {FirestoreDataConverter, DocumentData, QueryDocumentSnapshot} from "firebase-admin/firestore";

interface IStore {
    uid: string,
    name: string,
    totalSeats: number,
    totalAvailableSeats: number,
    totalSections: number,
}

interface IStoreExternal {
    name: string,
    totalSeats: number,
    totalAvailableSeats: number,
    totalSections: number,
}

class Store implements IStore {
    constructor(
        readonly uid: string,
        readonly name: string,
        readonly totalSeats: number,
        readonly totalAvailableSeats: number,
        readonly totalSections: number,
    ) {
    }
}


const storeConverter: FirestoreDataConverter<Store> = {
    toFirestore(store: Store): DocumentData {
        return {
            name: store.name,
            totalSeats: store.totalSeats,
            totalAvailableSeats: store.totalAvailableSeats,
            totalSections: store.totalSections,
        };
    },
    fromFirestore(
        snapshot: QueryDocumentSnapshot<IStoreExternal>
    ): Store {
        const data = snapshot.data();
        return new Store(snapshot.id,
            data.name,
            data.totalSeats,
            data.totalAvailableSeats,
            data.totalSections
        );
    },
};

export {Store, IStore, IStoreExternal, storeConverter};
