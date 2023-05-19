import {Change} from "firebase-functions/lib/common/change";
import {DocumentSnapshot} from "firebase-functions/lib/v1/providers/firestore";
import * as functions from "firebase-functions";
import {throwFunctionsHttpsError} from "../util/functions_helper";
import {FieldValue} from "firebase-admin/firestore";
import {ISectionExternal} from "../model/Section";


export const countSectionChangeHandler = async (
    change: Change<DocumentSnapshot>,
    // eslint-disable-next-line @typescript-eslint/no-unused-vars
    context: functions.EventContext<{ storeId: string, sectionId: string}>,
) => {
    const beforeSection = change.before.data() as ISectionExternal | null;
    const afterSection = change.after.data() as ISectionExternal | null;
    const storeRef = change.after.ref.parent.parent ?? throwFunctionsHttpsError("not-found", "store not found");

    let totalSeatsInc: number;
    let availableSeatsInc: number;
    let totalSectionsInc: number;

    if (change.after.exists && !change.before.exists) { // 없다가 생기면
        functions.logger.debug("Add section!");
        totalSeatsInc = afterSection?.totalSeats ?? 0;
        availableSeatsInc = afterSection?.totalAvailableSeats ?? 0;
        totalSectionsInc = 1;
    } else if (!change.after.exists && change.before.exists) { // 있다가 없으면
        functions.logger.debug("Delete section!");
        totalSeatsInc = -(beforeSection?.totalSeats ?? 0);
        availableSeatsInc = -(beforeSection?.totalAvailableSeats ?? 0);
        totalSectionsInc = -1;
    } else {
        functions.logger.debug("ELSE");
        totalSeatsInc = (afterSection?.totalSeats ?? 0) - (beforeSection?.totalSeats ?? 0);
        availableSeatsInc = (afterSection?.totalAvailableSeats ?? 0) - (beforeSection?.totalAvailableSeats ?? 0);
        totalSectionsInc = 0;
    }
    if (totalSeatsInc === 0 && availableSeatsInc === 0 && totalSectionsInc === 0) return null;

    await storeRef.update({
        totalSeats: FieldValue.increment(totalSeatsInc),
        totalAvailableSeats: FieldValue.increment(availableSeatsInc),
        totalSections: FieldValue.increment(totalSectionsInc),
    });
    functions.logger.info(`[Section ${change.after.id}] Counter updated.: ${availableSeatsInc}/${totalSeatsInc}/${totalSectionsInc}`);
    return null;
};
