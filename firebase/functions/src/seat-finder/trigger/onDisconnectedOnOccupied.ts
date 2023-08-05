import {onValueCreated} from "firebase-functions/v2/database";
import {REFERENCE_CURRENT_SESSION_NAME, REFERENCE_SEAT_FINDER_NAME} from "../../_database/NameConstant";
import {logger} from "firebase-functions/v2";
import SeatFinderHandler from "../SeatFinderHandler";
import {SeatFinderRequestType} from "../_enum/SeatFinderRequestType";


export const onDisconnectedOnOccupied =
    onValueCreated(
        {
            ref: `${REFERENCE_SEAT_FINDER_NAME}/{userId}/${REFERENCE_CURRENT_SESSION_NAME}/disconnectedOnOccupied`,
            region: "asia-southeast1",
        },
        (event) => {
            logger.debug(`[onDisconnectedOnOccupied] called ${JSON.stringify(event.data)}`);

            const current = Date.now();
            const handler = new SeatFinderHandler(event.params.userId);

            return handler.handleSeatFinderRequest(
                SeatFinderRequestType.LeaveAway,
                current,
                null,
                null,
                false,
            );
        }
    );
