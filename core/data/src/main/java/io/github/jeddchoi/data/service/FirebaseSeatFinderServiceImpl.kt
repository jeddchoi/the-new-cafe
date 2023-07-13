package io.github.jeddchoi.data.service

import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.functions.FirebaseFunctionsException
import io.github.jeddchoi.data.firebase.model.FirebaseSeatPosition
import io.github.jeddchoi.data.repository.CurrentUserRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withTimeout
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

const val CLOUD_FUNCTION_USER_ACTION_HANDLE = "onHandleRequest"

@Singleton
class FirebaseSeatFinderServiceImpl @Inject constructor(
    private val currentUserRepository: CurrentUserRepository,
    private val functions: FirebaseFunctions
) : SeatFinderService {

    private suspend fun sendUserActionRequest(
        request: SeatFinderRequest
    ) = kotlin.runCatching {
        withTimeout(5000L) {
            if (currentUserRepository.isUserSignedIn()) {
                functions.getHttpsCallable(CLOUD_FUNCTION_USER_ACTION_HANDLE).call(
                    Json.encodeToString(request)
                ).await().data as? RequestResult ?: RequestResult.fromResultCode(ResultCode.OK)
            } else {
                RequestResult.fromResultCode(ResultCode.UNAUTHENTICATED)
            }
        }
    }.fold(
        onSuccess = {
            // handles success or business errors
            ResultCode.getByValue(it.code)
        },
        onFailure = {
            // handles technical errors
            when (it) {
                is FirebaseFunctionsException -> {
                    when (it.code) {
                        FirebaseFunctionsException.Code.OK -> ResultCode.OK
                        FirebaseFunctionsException.Code.CANCELLED -> ResultCode.CANCELLED
                        FirebaseFunctionsException.Code.UNKNOWN -> ResultCode.UNKNOWN
                        FirebaseFunctionsException.Code.INVALID_ARGUMENT -> ResultCode.INVALID_ARGUMENT
                        FirebaseFunctionsException.Code.DEADLINE_EXCEEDED -> ResultCode.DEADLINE_EXCEEDED
                        FirebaseFunctionsException.Code.NOT_FOUND -> ResultCode.NOT_FOUND
                        FirebaseFunctionsException.Code.ALREADY_EXISTS -> ResultCode.ALREADY_EXISTS
                        FirebaseFunctionsException.Code.PERMISSION_DENIED -> ResultCode.PERMISSION_DENIED
                        FirebaseFunctionsException.Code.RESOURCE_EXHAUSTED -> ResultCode.RESOURCE_EXHAUSTED
                        FirebaseFunctionsException.Code.FAILED_PRECONDITION -> ResultCode.FAILED_PRECONDITION
                        FirebaseFunctionsException.Code.ABORTED -> ResultCode.ABORTED
                        FirebaseFunctionsException.Code.OUT_OF_RANGE -> ResultCode.OUT_OF_RANGE
                        FirebaseFunctionsException.Code.UNIMPLEMENTED -> ResultCode.UNIMPLEMENTED
                        FirebaseFunctionsException.Code.INTERNAL -> ResultCode.INTERNAL
                        FirebaseFunctionsException.Code.UNAVAILABLE -> ResultCode.UNAVAILABLE
                        FirebaseFunctionsException.Code.DATA_LOSS -> ResultCode.DATA_LOSS
                        FirebaseFunctionsException.Code.UNAUTHENTICATED -> ResultCode.UNAUTHENTICATED
                    }
                }

                is TimeoutCancellationException -> ResultCode.DEADLINE_EXCEEDED
                is IOException -> ResultCode.NETWORK_FAILURE
                is CancellationException -> ResultCode.CANCELLED
                else -> ResultCode.UNKNOWN
            }
        }
    )

    override suspend fun reserveSeat(
        seatPosition: FirebaseSeatPosition,
        endTime: Long?,
        durationInSeconds: Int?
    ) = sendUserActionRequest(
        SeatFinderRequest(
            requestType = SeatFinderRequestType.ReserveSeat,
            seatPosition = seatPosition,
            endTime = endTime,
            durationInSeconds = durationInSeconds
        )
    )

    override suspend fun occupySeat(endTime: Long?, durationInSeconds: Int?) =
        sendUserActionRequest(
            SeatFinderRequest(
                requestType = SeatFinderRequestType.OccupySeat,
                endTime = endTime,
                durationInSeconds = durationInSeconds
            )
        )

    override suspend fun quit() =
        sendUserActionRequest(
            SeatFinderRequest(
                requestType = SeatFinderRequestType.Quit,
            )
        )

    override suspend fun doBusiness(endTime: Long?, durationInSeconds: Int?) =
        sendUserActionRequest(
            SeatFinderRequest(
                requestType = SeatFinderRequestType.DoBusiness,
                endTime = endTime,
                durationInSeconds = durationInSeconds
            )
        )

    override suspend fun shiftToBusiness(endTime: Long?, durationInSeconds: Int?) =
        sendUserActionRequest(
            SeatFinderRequest(
                requestType = SeatFinderRequestType.ShiftToBusiness,
                endTime = endTime,
                durationInSeconds = durationInSeconds
            )
        )

    override suspend fun leaveAway(endTime: Long?, durationInSeconds: Int?) =
        sendUserActionRequest(
            SeatFinderRequest(
                requestType = SeatFinderRequestType.LeaveAway,
                endTime = endTime,
                durationInSeconds = durationInSeconds
            )
        )

    override suspend fun resumeUsing() =
        sendUserActionRequest(
            SeatFinderRequest(
                requestType = SeatFinderRequestType.ResumeUsing,
            )
        )

    override suspend fun changeReservationTimeoutTime(
        endTime: Long?,
        durationInSeconds: Int?
    ) =
        sendUserActionRequest(
            SeatFinderRequest(
                requestType = SeatFinderRequestType.ChangeOverallTimeoutTime,
                endTime = endTime,
                durationInSeconds = durationInSeconds
            )
        )

    override suspend fun changeOccupyTimeoutTime(
        endTime: Long?,
        durationInSeconds: Int?
    ) =
        sendUserActionRequest(
            SeatFinderRequest(
                requestType = SeatFinderRequestType.ChangeOverallTimeoutTime,
                endTime = endTime,
                durationInSeconds = durationInSeconds
            )
        )

    override suspend fun changeBusinessTimeoutTime(
        endTime: Long?,
        durationInSeconds: Int?
    ) =
        sendUserActionRequest(
            SeatFinderRequest(
                requestType = SeatFinderRequestType.ChangeTemporaryTimeoutTime,
                endTime = endTime,
                durationInSeconds = durationInSeconds
            )
        )

    override suspend fun changeAwayTimeoutTime(
        endTime: Long?,
        durationInSeconds: Int?
    ) =
        sendUserActionRequest(
            SeatFinderRequest(
                requestType = SeatFinderRequestType.ChangeTemporaryTimeoutTime,
                endTime = endTime,
                durationInSeconds = durationInSeconds
            )
        )
}