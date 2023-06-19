import {UserRecord} from "firebase-admin/auth";
import {logger} from "firebase-functions/v2";
import RealtimeDatabaseUtil from "../util/RealtimeDatabaseUtil";
import {IUserStateExternal} from "../model/UserState";

export const authUserCreatedHandler = (user: UserRecord) => {
    logger.info(`User ${user.uid} created.`);


    return RealtimeDatabaseUtil.getUserState(user.uid).set(<IUserStateExternal>{
        name: user.displayName ?? "Anonymous user",
        isOnline: false,
        status: null,
    });
};
