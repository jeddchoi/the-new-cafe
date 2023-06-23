import "reflect-metadata";


const COLLECTION_GROUP_STORE_NAME = "stores";
const COLLECTION_GROUP_SECTION_NAME = "sections";
const COLLECTION_GROUP_SEAT_NAME = "seats";

interface SeatPosition {
    readonly storeId: string;
    readonly sectionId: string;
    readonly seatId: string;
}

function pathToSeatPosition(serialized: string) {
    const parts = serialized.split("/");
    return <SeatPosition>{
        storeId: parts[1],
        sectionId: parts[3],
        seatId: parts[5],
    };
}

function seatPositionToPath(seatPosition: SeatPosition) {
    return `${COLLECTION_GROUP_STORE_NAME}/${seatPosition.storeId}/${COLLECTION_GROUP_SECTION_NAME}/${seatPosition.sectionId}/${COLLECTION_GROUP_SEAT_NAME}/${seatPosition.seatId}`;
}

export {
    SeatPosition,
    COLLECTION_GROUP_STORE_NAME,
    COLLECTION_GROUP_SECTION_NAME,
    COLLECTION_GROUP_SEAT_NAME,
    pathToSeatPosition,
    seatPositionToPath,
};
