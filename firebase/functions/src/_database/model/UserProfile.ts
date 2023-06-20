import {UserPrivateInfo} from "./UserPrivateInfo";

export interface UserProfile {
    displayName: string;
    isOnline: boolean;
    profilePhotoUrl: string | null;
    emailAddress: string | null;
    privateInfo: UserPrivateInfo;
}
