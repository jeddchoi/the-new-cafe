import {DatabaseEvent, DataSnapshot} from "firebase-functions/v2/database";
import {Change} from "firebase-functions/v2/firestore";
import RealtimeDatabaseUtil from "../util/RealtimeDatabaseUtil";
import {OverallState, TemporaryState} from "../model/UserState";
import {UserSession, UserStateChange} from "../model/UserSession";
import * as util from "util";
import {UserStateChangeReason} from "../model/UserStateChangeReason";
import {UserStateType} from "../model/UserStateType";

export const writeUserStateStatusHandler = (event: DatabaseEvent<Change<DataSnapshot>, { userId: string }>) => {
    const sessionRef = RealtimeDatabaseUtil.getUserSessionRef(event.params.userId);
    const stateChangesRef = sessionRef.child("stateChanges");

    const eventTimestamp = Date.parse(event.time);

    if (!event.data.before.exists() && event.data.after.exists()) { // if status is created
        const after = event.data.after.val() as {
            overall: OverallState,
            temporary: TemporaryState | null
        };
        return sessionRef.set(<UserSession>{
            startTime: after.overall.startTime,
            seatId: after.overall.seatId,
        }).then(() => {
            return stateChangesRef.push(<UserStateChange>{
                state: after.overall.state,
                timestamp: eventTimestamp,
                reason: after.overall.reason,
            });
        });
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
                return stateChangesRef.push(<UserStateChange>{
                    state: after.overall.state,
                    timestamp: eventTimestamp,
                    reason: after.overall.reason,
                });
            }
        } else if (!util.isDeepStrictEqual(before.temporary, after.temporary)) { // same overall, different temporary
            if (after.temporary && before.temporary?.state !== after.temporary.state) { // when after.temporary exists and different state
                return stateChangesRef.push(<UserStateChange>{
                    state: after.temporary.state,
                    timestamp: eventTimestamp,
                    reason: after.temporary.reason,
                });
            }
            if (before.temporary && !after.temporary) { // when deleted temporary
                return stateChangesRef.push(<UserStateChange>{
                    state: after.overall.state,
                    timestamp: eventTimestamp,
                    reason: UserStateChangeReason.Timeout,
                });
            }
        }
    } else if (event.data.before.exists() && !event.data.after.exists()) { // if status is deleted
        return stateChangesRef.push(<UserStateChange>{
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
        });
    }
    return Promise.resolve();
};
