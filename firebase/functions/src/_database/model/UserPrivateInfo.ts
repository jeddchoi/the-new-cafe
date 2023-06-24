import {BlockInfo} from "./BlockInfo";

export interface UserPrivateInfo {
    isAnonymous: boolean;
    creationTime: number;
    lastSignInTime: number;
    emailVerified: boolean;
    sex: number | null;
    block: BlockInfo | null;
}
