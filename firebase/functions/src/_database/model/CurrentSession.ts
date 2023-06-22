import {SeatPosition} from "../../_firestore/model/SeatPosition";
import {PartialUserState} from "./PartialUserState";
import {UserMainStateType} from "../../seat-finder/_enum/UserMainStateType";
import {UserSubStateType} from "../../seat-finder/_enum/UserSubStateType";

export interface CurrentSession {
    sessionId: string;
    seatPosition: SeatPosition;
    startSessionTime: number;
    mainState: PartialUserState<UserMainStateType>;
    subState: PartialUserState<UserSubStateType> | null;
}
