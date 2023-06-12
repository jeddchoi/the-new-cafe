import {Seat, SeatStateType} from "../model/Seat";
import FirestoreUtil from "../util/FirestoreUtil";
import {SeatPosition} from "../model/SeatPosition";
import {UserStateType} from "../model/UserStateType";


export default class SeatHandler {
    static getSeatData(seatPosition: SeatPosition): Promise<Seat | undefined> {
        return FirestoreUtil.getSeatDocRef(seatPosition).get()
            .then((value) => value.data());
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

    static updateSeatInSession(userId: string, seatPosition: SeatPosition, userState: UserStateType, reserveEndTime?: number | null, occupyEndTime?: number): Promise<void> {
        return this.transaction(seatPosition, (existing) => {
            if (!existing) return false;
            if (existing.isAvailable) return false;
            return existing.userId === userId;
        }, (existing) => {
            let seatState;
            switch (userState) {
                case UserStateType.None:
                case UserStateType.Blocked:
                    seatState = SeatStateType.Empty;
                    break;
                case UserStateType.Reserved:
                    seatState = SeatStateType.Reserved;
                    break;
                case UserStateType.Occupied:
                    seatState = SeatStateType.Occupied;
                    break;
                case UserStateType.Away:
                case UserStateType.OnBusiness:
                    seatState = SeatStateType.Away;
                    break;
            }
            return {
                state: seatState,
                reserveEndTime,
                occupyEndTime,
            };
        });
    }

    static transaction(
        seatPosition: SeatPosition,
        predicate: (existing: Seat | undefined) => boolean,
        update: (existing: Seat | undefined) => { [key in keyof Seat]?: Seat[key] }
    ): Promise<void> {
        return FirestoreUtil.runTransactionOnSingleRefDoc(FirestoreUtil.getSeatDocRef(seatPosition), predicate, update);
    }

    updateSeat(
        seatPosition: SeatPosition,
        updateContent: { [key in keyof Seat]?: Seat[key] }
    ): Promise<void> {
        return FirestoreUtil.getSeatDocRef(seatPosition).update(updateContent).then();
    }
}
