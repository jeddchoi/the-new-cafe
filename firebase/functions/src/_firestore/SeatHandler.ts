import {FirestoreUtil} from "./FirestoreUtil";
import {SeatPosition, seatPositionToPath} from "./model/SeatPosition";
import {Seat} from "./model/Seat";
import {https, logger} from "firebase-functions/v2";
import {ResultCode} from "../seat-finder/_enum/ResultCode";
import {SeatStateType} from "../seat-finder/_enum/SeatStateType";


let firestoreUtil: FirestoreUtil;

export default class SeatHandler {
    constructor(
        readonly userId: string,
    ) {
        firestoreUtil = firestoreUtil ?? new FirestoreUtil();
    }

    reserveSeat = (seatPosition: SeatPosition, endTime: number | null = null) => {
        logger.debug("[SeatHandler] reserveSeat", {
            seatPosition,
            endTime: endTime !== null ? new Date(endTime).toLocaleTimeString() : "no deadline",
        });
        return firestoreUtil.transaction<Seat>(seatPositionToPath(seatPosition), (existing) => {
            if (!existing) {
                logger.warn("[SeatHandler] Seat not found", {seatPosition, existing});
                throw new https.HttpsError("not-found", "Seat not found", {seatPosition, existing});
            }
            if (existing.userId || existing.state !== SeatStateType.Empty || !existing.isAvailable) {
                logger.warn("[SeatHandler] Seat not available", {seatPosition, existing});
                throw ResultCode.SEAT_NOT_AVAILABLE;
            }

            return <Seat>{
                ...existing,
                state: SeatStateType.Reserved,
                isAvailable: false,
                userId: this.userId,
                reserveEndTime: endTime,
            };
        });
    };

    occupySeat = (seatPosition: SeatPosition, endTime: number | null = null) => {
        logger.debug("[SeatHandler] occupySeat", {
            seatPosition,
            endTime: endTime !== null ? new Date(endTime).toLocaleTimeString() : "no deadline",
        });
        return firestoreUtil.transaction<Seat>(seatPositionToPath(seatPosition), (existing) => {
            if (!existing) {
                logger.error("[SeatHandler] Seat not found", {seatPosition, existing});
                throw new https.HttpsError("not-found", "Seat not found", {seatPosition, existing});
            }
            if (existing.userId !== this.userId) {
                logger.error("[SeatHandler] Permission denied", {seatPosition, existing});
                throw ResultCode.PERMISSION_DENIED;
            }
            if (existing.isAvailable || existing.state !== SeatStateType.Reserved) {
                logger.warn("[SeatHandler] Seat is in invalid state", {seatPosition, existing});
                throw ResultCode.INVALID_SEAT_STATE;
            }

            return <Seat>{
                ...existing,
                state: SeatStateType.Occupied,
                reserveEndTime: null,
                occupyEndTime: endTime,
            };
        });
    };

    away = (seatPosition: SeatPosition) => {
        logger.debug("[SeatHandler] leaveAway", {seatPosition});
        return firestoreUtil.transaction<Seat>(seatPositionToPath(seatPosition), (existing) => {
            if (!existing) {
                logger.error("[SeatHandler] Seat not found", {seatPosition, existing});
                throw new https.HttpsError("not-found", "Seat not found", {seatPosition, existing});
            }
            if (existing.userId !== this.userId) {
                logger.error("[SeatHandler] Permission denied", {seatPosition, existing});
                throw ResultCode.PERMISSION_DENIED;
            }
            if (existing.isAvailable) {
                logger.warn("[SeatHandler] Seat is in invalid state", {seatPosition, existing});
                throw ResultCode.INVALID_SEAT_STATE;
            }

            return <Seat>{
                ...existing,
                state: SeatStateType.Away,
            };
        });
    };

    resumeUsing = (seatPosition: SeatPosition) => {
        logger.debug("[SeatHandler] resumeUsing", {
            seatPosition,
        });
        return firestoreUtil.transaction<Seat>(seatPositionToPath(seatPosition), (existing) => {
            if (!existing) {
                logger.error("[SeatHandler] Seat not found", {seatPosition, existing});
                throw new https.HttpsError("not-found", "Seat not found", {seatPosition, existing});
            }
            if (existing.userId !== this.userId) {
                logger.error("[SeatHandler] Permission denied", {seatPosition, existing});
                throw ResultCode.PERMISSION_DENIED;
            }

            if (existing.isAvailable) {
                logger.warn("[SeatHandler] Something is broken", {seatPosition, existing});
                throw new https.HttpsError("data-loss", "Something is broken", {seatPosition, existing});
            }

            if (existing.state !== SeatStateType.Away) {
                logger.warn("[SeatHandler] Seat may be in invalid state", {seatPosition, existing});
            }

            return <Seat>{
                ...existing,
                state: SeatStateType.Occupied,
            };
        });
    };

    changeReserveEndTime = (seatPosition: SeatPosition, endTime: number | null = null) => this.changeEndTime(SeatStateType.Reserved, seatPosition, endTime);
    changeOccupyEndTime = (seatPosition: SeatPosition, endTime: number | null = null) => this.changeEndTime(SeatStateType.Occupied, seatPosition, endTime);

    freeSeat = (seatPosition: SeatPosition) => {
        logger.debug("[SeatHandler] freeSeat", {
            seatPosition,
        });
        return firestoreUtil.transaction<Seat>(seatPositionToPath(seatPosition), (existing) => {
            if (!existing) {
                logger.error("[SeatHandler] Seat not found", {seatPosition, existing});
                throw new https.HttpsError("not-found", "Seat not found", {seatPosition, existing});
            }
            if (existing.userId !== this.userId) {
                logger.error("[SeatHandler] Permission denied", {seatPosition, existing});
                throw ResultCode.PERMISSION_DENIED;
            }

            if (existing.isAvailable) {
                logger.warn("[SeatHandler] Something is broken", {seatPosition, existing});
                throw new https.HttpsError("data-loss", "Something is broken", {seatPosition, existing});
            }

            return <Seat>{
                ...existing,
                state: SeatStateType.Empty,
                isAvailable: true,
                userId: null,
                reserveEndTime: null,
                occupyEndTime: null,
            };
        });
    };

    private changeEndTime = (target: SeatStateType.Reserved | SeatStateType.Occupied, seatPosition: SeatPosition, endTime: number | null = null) => {
        logger.debug("[SeatHandler] changeReserveEndTime", {
            seatPosition,
            endTime: endTime !== null ? new Date(endTime).toLocaleTimeString() : "no deadline",
        });
        return firestoreUtil.transaction<Seat>(seatPositionToPath(seatPosition), (existing) => {
            if (!existing) {
                logger.error("[SeatHandler] Seat not found", {seatPosition, existing});
                throw new https.HttpsError("not-found", "Seat not found", {seatPosition, existing});
            }
            if (existing.userId !== this.userId) {
                logger.error("[SeatHandler] Permission denied", {seatPosition, existing});
                throw ResultCode.PERMISSION_DENIED;
            }

            if (existing.isAvailable || (target === SeatStateType.Reserved && existing.state !== SeatStateType.Reserved) || (target === SeatStateType.Occupied && ![SeatStateType.Occupied, SeatStateType.Away].includes(existing.state))) {
                logger.error("[SeatHandler] Session is in invalid state", {existing});
                throw ResultCode.INVALID_SESSION_STATE;
            }

            if (target === SeatStateType.Reserved) {
                return <Seat>{
                    ...existing,
                    reserveEndTime: endTime,
                };
            } else {
                return <Seat>{
                    ...existing,
                    occupyEndTime: endTime,
                };
            }
        });
    };
}
