import {RequestType} from "../model/RequestType";
import SeatHandler from "../handler/SeatHandler";
import UserStateHandler from "../handler/UserStateHandler";
import {SeatPosition} from "../model/UserState";
import {UserStateType} from "../model/UserStateType";
import {https, logger} from "firebase-functions/v2";

async function reserveSeat(userId: string, seatPosition: SeatPosition | null, current: number, endTime: number | null) {
    if (!seatPosition) throw new https.HttpsError("invalid-argument", "seatPosition is required");
    return SeatHandler.reserveSeat(userId, seatPosition, endTime).then((seatResult) => {
        if (!seatResult.committed) {
            throw new https.HttpsError("failed-precondition", "Failed to reserve seat");
        }
        return UserStateHandler.reserveSeat(userId, seatPosition, current, endTime).then((userResult) => {
            if (!userResult.committed && seatResult?.rollback) {
                logger.warn("userResult not committed -> seatResult rollback");
                return seatResult.rollback();
            } else {
                return;
            }
        }).catch((err) => {
            if (seatResult.rollback) {
                logger.warn("userResult error -> seatResult rollback");
                return seatResult.rollback().then(() => {
                    throw err;
                });
            }
            throw err;
        });
    });
}

function occupySeat(userId: string, current: number, endTime: number | null) {
    return UserStateHandler.occupySeat(userId, current, endTime)
        .then((userResult) => {
            if (!userResult.committed || !userResult.after?.overall.seatPosition) {
                throw new https.HttpsError("failed-precondition", "Failed to occupy seat");
            }
            return SeatHandler.occupySeat(userId, userResult.after?.overall.seatPosition, endTime).then((seatResult) => {
                if (!seatResult.committed && userResult?.rollback) {
                    logger.warn("seatResult not committed -> userResult rollback");
                    return userResult.rollback();
                } else {
                    return;
                }
            }).catch((err) => {
                if (userResult.rollback) {
                    logger.warn("seatResult error -> userResult rollback");
                    return userResult.rollback().then(() => {
                        throw err;
                    });
                }
                throw err;
            });
        });
}

function quit(userId: string) {
    return UserStateHandler.quit(userId).then((userResult) => {
        if (!userResult.committed || !userResult.before?.overall.seatPosition) {
            throw new https.HttpsError("failed-precondition", "Failed to quit seat");
        }
        return SeatHandler.freeSeat(userId, userResult.before?.overall.seatPosition).then((seatResult) => {
            if (!seatResult.committed && userResult?.rollback) {
                logger.warn("seatResult not committed -> userResult rollback");
                return userResult.rollback();
            } else {
                return;
            }
        }).catch((err) => {
            if (userResult.rollback) {
                logger.warn("seatResult error -> userResult rollback");
                return userResult.rollback().then(() => {
                    throw err;
                });
            }
            throw err;
        });
    });
}

function resumeUsing(userId: string) {
    return UserStateHandler.removeTemporaryState(userId).then((userResult) => {
        if (!userResult.committed || !userResult.after?.overall.seatPosition) {
            throw new https.HttpsError("failed-precondition", "Failed to resume using seat");
        }
        return SeatHandler.resumeUsing(userId, userResult.after?.overall.seatPosition).then((seatResult) => {
            if (!seatResult.committed && userResult?.rollback) {
                logger.warn("seatResult not committed -> userResult rollback");
                return userResult.rollback();
            } else {
                return;
            }
        }).catch((err) => {
            if (userResult.rollback) {
                logger.warn("seatResult error -> userResult rollback");
                return userResult.rollback().then(() => {
                    throw err;
                });
            }
            throw err;
        });
    });
}

function goTemporary(userId: string, targetState: UserStateType.Away | UserStateType.OnBusiness, current: number, endTime: number | null) {
    return UserStateHandler.updateUserTemporaryStateInSession(userId, targetState, current, endTime).then((userResult) => {
        if (!userResult.committed || !userResult.after?.overall.seatPosition) {
            throw new https.HttpsError("failed-precondition", "Failed to go temporary seat");
        }
        return SeatHandler.away(userId, userResult.after?.overall.seatPosition).then((seatResult) => {
            if (!seatResult.committed && userResult?.rollback) {
                logger.warn("seatResult not committed -> userResult rollback");
                return userResult.rollback();
            } else {
                return;
            }
        }).catch((err) => {
            if (userResult.rollback) {
                logger.warn("seatResult error -> userResult rollback");
                return userResult.rollback().then(() => {
                    throw err;
                });
            }
            throw err;
        });
    });
}

function changeOverallTimeoutTime(userId: string, endTime: number | null, current: number) {
    if (endTime && endTime <= current) {
        throw new https.HttpsError("invalid-argument", "endTime must be greater than current");
    }
    return UserStateHandler.updateOverallTimer(userId, endTime, current).then((userResult) => {
        if (!userResult.committed || !userResult.after?.overall.seatPosition) {
            throw new https.HttpsError("failed-precondition", "Failed to change overallTimeoutTime");
        }

        if (userResult.after.overall.state === UserStateType.Reserved) {
            return SeatHandler.updateReserveEndTime(userId, userResult.after?.overall.seatPosition, endTime).then((seatResult) => {
                if (!seatResult.committed && userResult?.rollback) {
                    logger.warn("seatResult not committed -> userResult rollback");
                    return userResult.rollback();
                } else {
                    return;
                }
            }).catch((err) => {
                if (userResult.rollback) {
                    logger.warn("seatResult error -> userResult rollback");
                    return userResult.rollback().then(() => {
                        throw err;
                    });
                }
                throw err;
            });
        } else if (userResult.after.overall.state === UserStateType.Occupied) {
            return SeatHandler.updateOccupyEndTime(userId, userResult.after?.overall.seatPosition, endTime).then((seatResult) => {
                if (!seatResult.committed && userResult?.rollback) {
                    logger.warn("seatResult not committed -> userResult rollback");
                    return userResult.rollback();
                } else {
                    return;
                }
            }).catch((err) => {
                if (userResult.rollback) {
                    logger.warn("seatResult error -> userResult rollback");
                    return userResult.rollback().then(() => {
                        throw err;
                    });
                }
                throw err;
            });
        } else {
            throw new https.HttpsError("internal", "Something went wrong");
        }
    });
}

function changeTemporaryTimeoutTime(userId: string, endTime: number | null, current: number) {
    if (endTime && endTime <= current) {
        throw new https.HttpsError("invalid-argument", "endTime must be greater than current");
    }
    return UserStateHandler.updateTemporaryTimer(userId, endTime, current);
}

export async function requestHandler(
    userId: string,
    requestType: RequestType,
    seatPosition: SeatPosition | null,
    endTime: number | null,
    current: number = new Date().getTime(),
) {
    logger.info(`[${current} -> ${endTime}] request: ${requestType} ${JSON.stringify(seatPosition)} by ${userId}`);

    switch (requestType) {
        case RequestType.ReserveSeat: {
            await reserveSeat(userId, seatPosition, current, endTime);
            break;
        }
        case RequestType.OccupySeat: {
            await occupySeat(userId, current, endTime);
            break;
        }
        case RequestType.Quit: {
            await quit(userId);
            break;
        }
        case RequestType.ResumeUsing: {
            await resumeUsing(userId);
            break;
        }
        case RequestType.LeaveAway: {
            await goTemporary(userId, UserStateType.Away, current, endTime);
            break;
        }
        case RequestType.DoBusiness:
        case RequestType.ShiftToBusiness: {
            await goTemporary(userId, UserStateType.OnBusiness, current, endTime);
            break;
        }
        case RequestType.ChangeOverallTimeoutTime: {
            await changeOverallTimeoutTime(userId, endTime, current);
            break;
        }
        case RequestType.ChangeTemporaryTimeoutTime: {
            await changeTemporaryTimeoutTime(userId, endTime, current);
            break;
        }
    }
}
