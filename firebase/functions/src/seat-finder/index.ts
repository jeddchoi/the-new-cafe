import {CallableRequest, onCall} from "firebase-functions/v2/https";
import {ISeatFinderRequest} from "./_model/SeatFinderRequest";
import SeatFinderHandler from "./SeatFinderHandler";
import {https, logger} from "firebase-functions/v2";

export const onHandleRequest =
    onCall<ISeatFinderRequest>((request: CallableRequest<ISeatFinderRequest>) => {
        if (!request.auth?.uid) {
            throw new https.HttpsError("unauthenticated", "User must be authenticated");
        }

        const handler = new SeatFinderHandler(request.auth.uid);
        logger.log("SeatFinderRequest =======", {request: request.data});
        return handler.handleSeatFinderRequest(request.data);
    });
// SeatFinder.onHandleRequest({seatPosition: {storeId: "i9sAij5mVBijR85hgraE", sectionId: "FMLYWLzKmiou1PTcrFR8", seatId: "ZlblGsMYd7IlO1DEho4H"}, durationInSeconds: 100, requestType: "ReserveSeat"})
