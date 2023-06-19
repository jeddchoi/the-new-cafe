import {SeatStateType} from "./SeatStateType";

export interface Seat {
    name: string;
    minor: string;
    state: SeatStateType;
    isAvailable: boolean;
    userId: string | null;
    reserveEndTime: number | null;
    occupyEndTime: number | null;
}
