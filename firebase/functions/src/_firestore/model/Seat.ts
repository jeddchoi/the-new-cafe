import {SeatStateType} from "../../seat-finder/_enum/SeatStateType";

export interface Seat {
    name: string;
    minor: string;
    macAddress: string;
    state: SeatStateType;
    isAvailable: boolean;
    userId: string | null;
    reserveEndTime: number | null;
    occupyEndTime: number | null;
}
