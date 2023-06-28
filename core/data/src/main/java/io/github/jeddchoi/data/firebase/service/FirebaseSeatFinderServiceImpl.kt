package io.github.jeddchoi.data.firebase.service

import android.util.Log
import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.functions.FirebaseFunctionsException
import io.github.jeddchoi.data.firebase.model.FirebaseRequestResult
import io.github.jeddchoi.data.firebase.model.FirebaseSeatPosition
import io.github.jeddchoi.data.repository.CurrentUserRepository
import io.github.jeddchoi.data.service.seatfinder.ResultCode
import io.github.jeddchoi.data.service.seatfinder.SeatFinderRequest
import io.github.jeddchoi.data.service.seatfinder.SeatFinderResult
import io.github.jeddchoi.data.service.seatfinder.SeatFinderService
import io.github.jeddchoi.data.service.seatfinder.SeatFinderUserRequestType
import io.github.jeddchoi.data.util.toJsonElement
import io.github.jeddchoi.model.SeatFinderRequestType
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withTimeout
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromJsonElement
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

const val CLOUD_FUNCTION_USER_ACTION_HANDLE = "SeatFinder-onHandleRequest"

@ExperimentalSerializationApi
@Singleton
class FirebaseSeatFinderServiceImpl @Inject constructor(
    private val currentUserRepository: CurrentUserRepository,
    private val functions: FirebaseFunctions
) : SeatFinderService {

    private suspend fun sendUserActionRequest(
        request: SeatFinderRequest
    ) = kotlin.runCatching {
        withTimeout(5000L) {
            if (!currentUserRepository.isUserSignedIn()) {
                return@withTimeout SeatFinderResult(resultCode = ResultCode.UNAUTHENTICATED);
            }
            val inputData = hashMapOf(
                "requestType" to request.requestType.name,
                "seatPosition" to request.seatPosition?.let {
                    hashMapOf(
                        "storeId" to it.storeId,
                        "sectionId" to it.sectionId,
                        "seatId" to it.seatId,
                    )
                },
                "endTime" to request.endTime,
                "durationInSeconds" to request.durationInSeconds
            )

            val json = Json { ignoreUnknownKeys = true }
            val data = functions.getHttpsCallable(CLOUD_FUNCTION_USER_ACTION_HANDLE).call(inputData).await().data.toJsonElement(json)
            Log.i("SeatFinder", data.toString())
            val result = json.decodeFromJsonElement<FirebaseRequestResult>(data)
            Log.i("SeatFinder", result.toString())
            result.toSeatFinderResult()
        }
    }.fold(
        onSuccess = {
            // handles success or business errors
            it
        },
        onFailure = {
            // handles technical errors
            Log.e("SeatFinder", it.stackTraceToString())
            SeatFinderResult(
                resultCode =
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
            )
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

    override suspend fun requestInSession(
        seatFinderRequestType: SeatFinderUserRequestType,
        endTime: Long?,
        durationInSeconds: Int?
    ): SeatFinderResult {
        return when (seatFinderRequestType) {
            SeatFinderUserRequestType.Reserve -> throw IllegalArgumentException("Reserved seat is not request in session")
            SeatFinderUserRequestType.Occupy -> occupySeat(endTime, durationInSeconds)
            SeatFinderUserRequestType.Quit -> quit()
            SeatFinderUserRequestType.DoBusiness -> doBusiness(endTime, durationInSeconds)
            SeatFinderUserRequestType.LeaveAway -> leaveAway(endTime, durationInSeconds)
            SeatFinderUserRequestType.ResumeUsing -> resumeUsing()
            SeatFinderUserRequestType.ChangeReservationEndTime -> changeReservationEndTime(endTime, durationInSeconds)
            SeatFinderUserRequestType.ChangeOccupyEndTime -> changeOccupyEndTime(endTime, durationInSeconds)
            SeatFinderUserRequestType.ChangeBusinessEndTime -> changeBusinessEndTime(endTime, durationInSeconds)
            SeatFinderUserRequestType.ChangeAwayEndTime -> changeAwayEndTime(endTime, durationInSeconds)
        }
    }

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

    override suspend fun changeReservationEndTime(
        endTime: Long?,
        durationInSeconds: Int?
    ) =
        sendUserActionRequest(
            SeatFinderRequest(
                requestType = SeatFinderRequestType.ChangeMainStateEndTime,
                endTime = endTime,
                durationInSeconds = durationInSeconds
            )
        )

    override suspend fun changeOccupyEndTime(
        endTime: Long?,
        durationInSeconds: Int?
    ) =
        sendUserActionRequest(
            SeatFinderRequest(
                requestType = SeatFinderRequestType.ChangeMainStateEndTime,
                endTime = endTime,
                durationInSeconds = durationInSeconds
            )
        )

    override suspend fun changeBusinessEndTime(
        endTime: Long?,
        durationInSeconds: Int?
    ) =
        sendUserActionRequest(
            SeatFinderRequest(
                requestType = SeatFinderRequestType.ChangeSubStateEndTime,
                endTime = endTime,
                durationInSeconds = durationInSeconds
            )
        )

    override suspend fun changeAwayEndTime(
        endTime: Long?,
        durationInSeconds: Int?
    ) =
        sendUserActionRequest(
            SeatFinderRequest(
                requestType = SeatFinderRequestType.ChangeSubStateEndTime,
                endTime = endTime,
                durationInSeconds = durationInSeconds
            )
        )
}