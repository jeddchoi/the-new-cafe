type SeatId = {
    storeId: string;
    sectionId: string;
    seatId: string;
}

// SeatId 객체를 문자열로 직렬화하는 함수
function serializeSeatId(seat: SeatId): string {
    return JSON.stringify(seat);
}

// 직렬화된 문자열을 SeatId 객체로 역직렬화하는 함수
function deserializeSeatId(serializedSeat: string): SeatId {
    return JSON.parse(serializedSeat) as SeatId;
}

export {
    SeatId,
    serializeSeatId,
    deserializeSeatId,
};
