export interface Store {
    name: string;
    uuid: string;
    acceptsReservation: boolean;
    photoUrl: string;
    totalSeats: number;
    totalAvailableSeats: number;
    totalSections: number;
}
