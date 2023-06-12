import {DatabaseEvent, DataSnapshot} from "firebase-functions/v2/database";
import {Change} from "firebase-functions/v2/firestore";
import RealtimeDatabaseUtil from "../util/RealtimeDatabaseUtil";
import {OverallState, TemporaryState} from "../model/UserState";
import {UserSession, UserStateChange} from "../model/UserSession";
import * as util from "util";
import {UserStateChangeReason} from "../model/UserStateChangeReason";
import {UserStateType} from "../model/UserStateType";
import SeatHandler from "../handler/SeatHandler";
import {deserializeSeatId} from "../model/SeatPosition";
import {logger} from "firebase-functions/v2";

export const userStateStatusWrittenHandler = (event: DatabaseEvent<Change<DataSnapshot>, { userId: string }>) => {
    logger.debug(`userStateStatusWrittenHandler ${event.params.userId}`);
    const promises = [];
    const sessionRef = RealtimeDatabaseUtil.getUserSessionRef(event.params.userId);
    const stateChangesRef = sessionRef.child("stateChanges");

    const eventTimestamp = Date.parse(event.time);

    if (!event.data.before.exists() && event.data.after.exists()) { // if status is created (e.g. Reserve)
        const after = event.data.after.val() as {
            overall: OverallState,
            temporary: TemporaryState | null
        };
        promises.push(sessionRef.set(<UserSession>{
            startTime: after.overall.startTime,
            seatPosition: after.overall.seatPosition,
        }).then(() => {
            return stateChangesRef.push(<UserStateChange>{
                state: after.overall.state,
                timestamp: after.overall.startTime,
                reason: after.overall.reason,
            });
        }));
    } else if (event.data.before.exists() && event.data.after.exists()) { // if status is updated
        const before = event.data.before.val() as {
            overall: OverallState,
            temporary: TemporaryState | null
        };
        const after = event.data.after.val() as {
            overall: OverallState,
            temporary: TemporaryState | null
        };

        if (!util.isDeepStrictEqual(before.overall, after.overall)) { // different overall
            if (before.overall.state !== after.overall.state) {
                promises.push(stateChangesRef.push(<UserStateChange>{
                    state: after.overall.state,
                    timestamp: after.overall.startTime,
                    reason: after.overall.reason,
                }));
            }
        } else if (!util.isDeepStrictEqual(before.temporary, after.temporary)) { // same overall, different temporary
            if (after.temporary && before.temporary?.state !== after.temporary.state) { // when after.temporary exists and different state
                promises.push(stateChangesRef.push(<UserStateChange>{
                    state: after.temporary.state,
                    timestamp: after.temporary.startTime,
                    reason: after.temporary.reason,
                }));
            }
            if (before.temporary && !after.temporary) { // when deleted temporary
                promises.push(stateChangesRef.push(<UserStateChange>{
                    state: after.overall.state,
                    timestamp: eventTimestamp,
                    reason: UserStateChangeReason.Timeout,
                }));

                if (after.overall.seatPosition) {
                    promises.push(
                        SeatHandler.resumeUsing(event.params.userId, deserializeSeatId(after.overall.seatPosition))
                    );
                }
            }
        }
    } else if (event.data.before.exists() && !event.data.after.exists()) { // if status is deleted (e.g. Stop using seat)
        const before = event.data.before.val() as {
            overall: OverallState,
            temporary: TemporaryState | null
        };
        promises.push(stateChangesRef.push(<UserStateChange>{
            state: UserStateType.None,
            timestamp: eventTimestamp,
            reason: UserStateChangeReason.Timeout,
        }).then(() => {
            return sessionRef.once("value");
        }).then((snapshot) => {
            const userSession = snapshot.val() as UserSession;
            return RealtimeDatabaseUtil.getUserHistoryRef(event.params.userId).push(<UserSession>{
                ...userSession,
                endTime: eventTimestamp,
            });
        }).then(() => {
            return sessionRef.remove();
        }));
        if (before.overall.seatPosition) {
            promises.push(SeatHandler.freeSeat(event.params.userId, deserializeSeatId(before.overall.seatPosition)));
        }
    }
    return Promise.all(promises);
};
