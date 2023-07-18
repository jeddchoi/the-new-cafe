import {UserMainStateType} from "./UserMainStateType";
import {UserSubStateType} from "./UserSubStateType";
import {SeatFinderRequestType} from "./SeatFinderRequestType";

export type UserStateType = "User_None" | UserMainStateType | UserSubStateType


export const getWillRequestType = (fromState: UserStateType) => {
    switch (fromState) {
        case "User_None":
            return null;
        case UserMainStateType.Reserved:
            return SeatFinderRequestType.Quit;
        case UserMainStateType.Occupied:
            return SeatFinderRequestType.Quit;
        case UserSubStateType.Away:
            return SeatFinderRequestType.Quit;
        case UserSubStateType.OnBusiness:
            return SeatFinderRequestType.ResumeUsing;
    }
};
