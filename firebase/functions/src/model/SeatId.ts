const COLLECTION_GROUP_STORE_NAME = "stores";
const COLLECTION_GROUP_SECTION_NAME = "sections";
const COLLECTION_GROUP_SEAT_NAME = "seats";


interface SeatId {
    storeId: string;
    sectionId: string;
    seatId: string;
}

// SeatId 객체를 문자열로 직렬화하는 함수
function serializeSeatId(seat: SeatId): string {
    return `${COLLECTION_GROUP_STORE_NAME}/${seat.storeId}/${COLLECTION_GROUP_SECTION_NAME}/${seat.sectionId}/${COLLECTION_GROUP_SEAT_NAME}/${seat.seatId}`;
}

// 직렬화된 문자열을 SeatId 객체로 역직렬화하는 함수
function deserializeSeatId(serializedSeat: string): SeatId {
    const parsed = serializedSeat.split("/", );
    return <SeatId>{
        storeId: parsed[1],
        sectionId: parsed[3],
        seatId: parsed[5],
    };
}

export {
    SeatId,
    serializeSeatId,
    deserializeSeatId,
    COLLECTION_GROUP_STORE_NAME,
    COLLECTION_GROUP_SECTION_NAME,
    COLLECTION_GROUP_SEAT_NAME,
};
