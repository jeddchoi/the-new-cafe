import "reflect-metadata";
import {COLLECTION_GROUP_SEAT_NAME, COLLECTION_GROUP_SECTION_NAME, COLLECTION_GROUP_STORE_NAME} from "../NameConstant";


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
    pathToSeatPosition,
    seatPositionToPath,
};
