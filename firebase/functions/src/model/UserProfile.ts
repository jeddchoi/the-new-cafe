export interface UserProfile {
    isAnonymous: boolean;
    displayName: string;
    creationTime: number;
    lastSignInTime: number;
    isOnline: boolean;
    emailAddress: string | null;
    profilePhotoUrl: string | null;
}
