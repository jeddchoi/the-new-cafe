import {FirestoreDataConverter, QueryDocumentSnapshot} from "firebase-admin/firestore";

interface IStore {
    uid: string,
    name: string,
    totalSeats: number,
    totalAvailableSeats: number,
    totalSections: number,
    acceptsReservation: boolean,
    photoUrl: string,
}

interface IStoreExternal {
    name: string,
    totalSeats: number,
    totalAvailableSeats: number,
    totalSections: number,
    acceptsReservation: boolean,
    photoUrl: string,
}

class Store implements IStore {
    constructor(
        readonly uid: string,
        readonly name: string,
        readonly totalSeats: number,
        readonly totalAvailableSeats: number,
        readonly totalSections: number,
        readonly acceptsReservation: boolean,
        readonly photoUrl: string,
    ) {
    }
}


const storeConverter: FirestoreDataConverter<Store> = {
    toFirestore(store: Store) {
        return <IStoreExternal>{
            name: store.name,
            totalSeats: store.totalSeats,
            totalAvailableSeats: store.totalAvailableSeats,
            totalSections: store.totalSections,
            acceptsReservation: store.acceptsReservation,
            photoUrl: store.photoUrl,
        };
    },
    fromFirestore(
        snapshot: QueryDocumentSnapshot<IStoreExternal>
    ) {
        const data = snapshot.data();
        return new Store(snapshot.id,
            data.name,
            data.totalSeats,
            data.totalAvailableSeats,
            data.totalSections,
            data.acceptsReservation,
            data.photoUrl,
        );
    },
};

export {Store, IStore, IStoreExternal, storeConverter};
