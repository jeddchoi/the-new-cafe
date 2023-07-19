package io.github.jeddchoi.data.firebase.service

import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.functions.FirebaseFunctionsException
import com.google.firebase.functions.ktx.functions
import com.google.firebase.ktx.Firebase
import io.github.jeddchoi.data.firebase.model.FirebaseRequestResult
import io.github.jeddchoi.data.repository.CurrentUserRepository
import io.github.jeddchoi.data.service.seatfinder.ResultCode
import io.github.jeddchoi.data.service.seatfinder.SeatFinderRequest
import io.github.jeddchoi.data.service.seatfinder.SeatFinderResult
import io.github.jeddchoi.data.service.seatfinder.SeatFinderService
import io.github.jeddchoi.data.service.seatfinder.SeatFinderUserRequestType
import io.github.jeddchoi.data.util.toJsonElement
import io.github.jeddchoi.model.SeatFinderRequestType
import io.github.jeddchoi.model.SeatPosition
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromJsonElement
import timber.log.Timber
import java.io.IOException
import javax.inject.Inject

const val CLOUD_FUNCTION_USER_ACTION_HANDLE = "SeatFinder-onHandleRequest"

@OptIn(ExperimentalSerializationApi::class)
class FirebaseSeatFinderServiceImpl @Inject constructor(
    private val currentUserRepository: CurrentUserRepository,
) : SeatFinderService {
    private val functions: FirebaseFunctions = Firebase.functions("asia-northeast3")

    private suspend fun sendUserActionRequest(
        request: SeatFinderRequest
    ) = withContext(Dispatchers.IO) {
        kotlin.runCatching {
            Timber.v("✅ $request")
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
                val data =
                    functions.getHttpsCallable(CLOUD_FUNCTION_USER_ACTION_HANDLE).call(inputData)
                        .await().data.toJsonElement(json)
                val result = json.decodeFromJsonElement<FirebaseRequestResult>(data)
                result.toSeatFinderResult()
            }
        }.fold(
            onSuccess = {
                // handles success or business errors
                it
            },
            onFailure = {
                // handles technical errors
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
        ).also { result ->
            Timber.i(result.toString())
        }
    }

    override suspend fun reserveSeat(
        seatPosition: SeatPosition,
        endTime: Long?,
        durationInSeconds: Int?
    ): SeatFinderResult {
        Timber.v("✅ $seatPosition, $endTime, $durationInSeconds")
        return sendUserActionRequest(
            SeatFinderRequest(
                requestType = SeatFinderRequestType.ReserveSeat,
                seatPosition = seatPosition,
                endTime = endTime,
                durationInSeconds = durationInSeconds
            )
        )
    }

    override suspend fun requestInSession(
        seatFinderRequestType: SeatFinderUserRequestType,
        endTime: Long?,
        durationInSeconds: Int?
    ): SeatFinderResult {
        Timber.v("✅ $seatFinderRequestType, $endTime, $durationInSeconds")
        return when (seatFinderRequestType) {
            SeatFinderUserRequestType.Reserve -> throw IllegalArgumentException("Reserved seat is not request in session")
            SeatFinderUserRequestType.Occupy -> occupySeat(endTime, durationInSeconds)
            SeatFinderUserRequestType.Quit -> quit()
            SeatFinderUserRequestType.DoBusiness -> doBusiness(endTime, durationInSeconds)
            SeatFinderUserRequestType.LeaveAway -> leaveAway(endTime, durationInSeconds)
            SeatFinderUserRequestType.ResumeUsing -> resumeUsing()
            SeatFinderUserRequestType.ChangeReservationEndTime -> changeReservationEndTime(
                endTime,
                durationInSeconds
            )

            SeatFinderUserRequestType.ChangeOccupyEndTime -> changeOccupyEndTime(
                endTime,
                durationInSeconds
            )

            SeatFinderUserRequestType.ChangeBusinessEndTime -> changeBusinessEndTime(
                endTime,
                durationInSeconds
            )

            SeatFinderUserRequestType.ChangeAwayEndTime -> changeAwayEndTime(
                endTime,
                durationInSeconds
            )
        }
    }

    override suspend fun occupySeat(endTime: Long?, durationInSeconds: Int?): SeatFinderResult {
        Timber.v("✅ $endTime, $durationInSeconds")
        return sendUserActionRequest(
            SeatFinderRequest(
                requestType = SeatFinderRequestType.OccupySeat,
                endTime = endTime,
                durationInSeconds = durationInSeconds
            )
        )
    }

    override suspend fun quit(): SeatFinderResult {
        Timber.v("✅")
        return sendUserActionRequest(
            SeatFinderRequest(
                requestType = SeatFinderRequestType.Quit,
            )
        )
    }

    override suspend fun doBusiness(endTime: Long?, durationInSeconds: Int?): SeatFinderResult {
        Timber.v("✅ $endTime, $durationInSeconds")
        return sendUserActionRequest(
            SeatFinderRequest(
                requestType = SeatFinderRequestType.DoBusiness,
                endTime = endTime,
                durationInSeconds = durationInSeconds
            )
        )
    }

    override suspend fun leaveAway(endTime: Long?, durationInSeconds: Int?): SeatFinderResult {
        Timber.v("✅ $endTime, $durationInSeconds")
        return sendUserActionRequest(
            SeatFinderRequest(
                requestType = SeatFinderRequestType.LeaveAway,
                endTime = endTime,
                durationInSeconds = durationInSeconds
            )
        )
    }

    override suspend fun resumeUsing(): SeatFinderResult {
        Timber.v("✅")
        return sendUserActionRequest(
            SeatFinderRequest(
                requestType = SeatFinderRequestType.ResumeUsing,
            )
        )
    }

    override suspend fun changeReservationEndTime(
        endTime: Long?,
        durationInSeconds: Int?
    ): SeatFinderResult {
        Timber.v("✅ $endTime, $durationInSeconds")
        return sendUserActionRequest(
            SeatFinderRequest(
                requestType = SeatFinderRequestType.ChangeMainStateEndTime,
                endTime = endTime,
                durationInSeconds = durationInSeconds
            )
        )
    }

    override suspend fun changeOccupyEndTime(
        endTime: Long?,
        durationInSeconds: Int?
    ): SeatFinderResult {
        Timber.v("✅ $endTime, $durationInSeconds")
        return sendUserActionRequest(
            SeatFinderRequest(
                requestType = SeatFinderRequestType.ChangeMainStateEndTime,
                endTime = endTime,
                durationInSeconds = durationInSeconds
            )
        )
    }

    override suspend fun changeBusinessEndTime(
        endTime: Long?,
        durationInSeconds: Int?
    ): SeatFinderResult {
        Timber.v("✅ $endTime, $durationInSeconds")
        return sendUserActionRequest(
            SeatFinderRequest(
                requestType = SeatFinderRequestType.ChangeSubStateEndTime,
                endTime = endTime,
                durationInSeconds = durationInSeconds
            )
        )
    }

    override suspend fun changeAwayEndTime(
        endTime: Long?,
        durationInSeconds: Int?
    ): SeatFinderResult {
        Timber.v("✅ $endTime, $durationInSeconds")
        return sendUserActionRequest(
            SeatFinderRequest(
                requestType = SeatFinderRequestType.ChangeSubStateEndTime,
                endTime = endTime,
                durationInSeconds = durationInSeconds
            )
        )
    }
}