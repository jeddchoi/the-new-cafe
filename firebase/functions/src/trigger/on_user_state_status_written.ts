import {DatabaseEvent, DataSnapshot} from "firebase-functions/v2/database";
import {Change} from "firebase-functions/v2/firestore";
import {OverallState, State} from "../model/UserState";
import * as util from "util";
import {UserStateChangeReason} from "../model/UserStateChangeReason";
import {UserStateType} from "../model/UserStateType";
import {logger} from "firebase-functions/v2";
import UserSessionHandler from "../handler/UserSessionHandler";

export const userStateStatusWrittenHandler = (event: DatabaseEvent<Change<DataSnapshot>, { userId: string }>) => {
    logger.debug(`userStateStatusWrittenHandler ${event.params.userId}`);
    const promises = [];
    const eventTimestamp = Date.parse(event.time);

    const before = event.data.before.val() as {
        overall: OverallState,
        temporary: State | null
    } | null;

    const after = event.data.after.val() as {
        overall: OverallState,
        temporary: State | null
    } | null;

    const sessionHandler = new UserSessionHandler(event.params.userId);

    if (!event.data.before.exists() && event.data.after.exists()) { // if status is created (e.g. Reserve)
        if (after?.overall) {
            promises.push(sessionHandler.createSession(after.overall));
        }
    } else if (event.data.before.exists() && event.data.after.exists()) { // if status is updated
        if (after && !util.isDeepStrictEqual(before?.overall, after.overall)) { // different overall
            if (before?.overall.state !== after.overall.state) {
                promises.push(sessionHandler.addStateChange(after?.overall));
            }
        } else if (after && !util.isDeepStrictEqual(before?.temporary, after.temporary)) { // same overall, different temporary
            if (after.temporary && before?.temporary?.state !== after.temporary.state) { // when after.temporary exists and different state
                promises.push(sessionHandler.addStateChange(after.temporary));
            }
            if (before?.temporary && !after.temporary) { // when deleted temporary
                promises.push(
                    sessionHandler.addStateChangeOnDeletion(
                        after.overall.state,
                        eventTimestamp,
                        UserStateChangeReason.Timeout, // TODO: not right
                    )
                );
            }
        }
    } else if (event.data.before.exists() && !event.data.after.exists()) { // if status is deleted (e.g. Stop using seat)
        promises.push(sessionHandler.addStateChangeOnDeletion(
            UserStateType.None,
            eventTimestamp,
            UserStateChangeReason.Timeout // TODO: not right
        ).then(() => {
            return sessionHandler.cleanupSession(eventTimestamp);
        }));
    }
    return Promise.all(promises);
};
