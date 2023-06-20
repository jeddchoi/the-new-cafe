const COLLECTION_GROUP_STORE_NAME = "stores";
const COLLECTION_GROUP_SECTION_NAME = "sections";
const COLLECTION_GROUP_SEAT_NAME = "seats";

class SeatPosition {
    constructor(
        readonly storeId: string,
        readonly sectionId: string,
        readonly seatId: string,
    ) {
    }

    static deserialize(serialized: string) {
        const parts = serialized.split("/");
        return new SeatPosition(parts[1], parts[3], parts[5]);
    }

    serialize() {
        return `${COLLECTION_GROUP_STORE_NAME}/${this.storeId}/${COLLECTION_GROUP_SECTION_NAME}/${this.sectionId}/${COLLECTION_GROUP_SEAT_NAME}/${this.seatId}`;
    }
}

export {
    SeatPosition,
    COLLECTION_GROUP_STORE_NAME,
    COLLECTION_GROUP_SECTION_NAME,
    COLLECTION_GROUP_SEAT_NAME,
};
