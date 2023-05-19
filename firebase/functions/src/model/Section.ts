import {firestore} from "firebase-admin";

interface ISection {
    uid: string,
    storeId: string,
    name: string,
    totalSeats: number,
    totalAvailableSeats: number,
}

interface ISectionExternal {
    name: string,
    totalSeats: number,
    totalAvailableSeats: number,
}

class Section implements ISection {
    constructor(
        readonly uid: string,
        readonly storeId: string,
        readonly name: string,
        readonly totalSeats: number,
        readonly totalAvailableSeats: number,
    ) {
    }
}


const sectionConverter: firestore.FirestoreDataConverter<Section> = {
    toFirestore(section: Section): firestore.DocumentData {
        return {
            name: section.name,
            totalSeats: section.totalSeats,
            totalAvailableSeats: section.totalAvailableSeats,
        };
    },
    fromFirestore(
        snapshot: firestore.QueryDocumentSnapshot<ISectionExternal>
    ): Section {
        const data = snapshot.data();
        return new Section(snapshot.id, data.name, snapshot.ref.parent.id, data.totalSeats, data.totalAvailableSeats);
    },
};

export {Section, ISection, ISectionExternal, sectionConverter};
