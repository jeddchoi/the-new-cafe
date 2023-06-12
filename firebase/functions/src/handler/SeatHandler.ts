import {ISeatExternal, SeatStateType} from "../model/Seat";
import FirestoreUtil from "../util/FirestoreUtil";
import {SeatPosition} from "../model/SeatPosition";


export default class SeatHandler {
    static getSeatData(seatPosition: SeatPosition): Promise<ISeatExternal | undefined> {
        return FirestoreUtil.getSeatDocRef(seatPosition).get()
            .then((value) => value.data());
    }

    static reserveSeat(userId: string, seatPosition: SeatPosition, endTime: number | null): Promise<boolean> {
        return this.transaction(seatPosition, (existing) => {
            if (!existing) return false;
            if (existing.userId) return false;
            if (existing.occupyEndTime) return false;
            if (existing.reserveEndTime) return false;
            if (existing.state !== SeatStateType.Empty) return false;

            return existing.isAvailable;
        }, (existing) => {
            return {
                ...existing,
                state: SeatStateType.Reserved,
                isAvailable: false,
                userId,
                reserveEndTime: endTime,
            };
        });
    }

    static freeSeat(userId: string, seatPosition: SeatPosition): Promise<void> {
        return this.transaction(seatPosition, (existing) => {
            if (!existing) return false;
            if (existing.isAvailable) return false;
            return existing.userId === userId;
        }, (existing) => {
            return {
                userId: null,
                reserveEndTime: null,
                occupyEndTime: null,
            };
        }).then();
    }

    static updateSeatInSession(userId: string, seatPosition: SeatPosition, seatState: SeatStateType, reserveEndTime?: number | null, occupyEndTime?: number | null): Promise<boolean> {
        return this.transaction(seatPosition, (existing) => {
            if (!existing) return false;
            if (existing.isAvailable) return false;
            return existing.userId === userId;
        }, (existing) => {
            return {
                state: seatState,
                reserveEndTime,
                occupyEndTime,
            };
        });
    }

    static transaction(
        seatPosition: SeatPosition,
        predicate: (existing: ISeatExternal | undefined) => boolean,
        update: (existing: ISeatExternal | undefined) => { [key in keyof ISeatExternal]?: ISeatExternal[key] }
    ): Promise<boolean> {
        return FirestoreUtil.runTransactionOnSingleRefDoc(FirestoreUtil.getSeatDocRef(seatPosition), predicate, update);
    }

    updateSeat(
        seatPosition: SeatPosition,
        updateContent: { [key in keyof ISeatExternal]?: ISeatExternal[key] }
    ): Promise<void> {
        return FirestoreUtil.getSeatDocRef(seatPosition).update(updateContent).then();
    }
}
