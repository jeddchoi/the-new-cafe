package io.github.jeddchoi.ble

import com.benasher44.uuid.bytes
import com.benasher44.uuid.uuidFrom
import com.juul.kable.Filter
import com.juul.kable.Peripheral
import com.juul.kable.Scanner
import com.juul.kable.State
import io.github.jeddchoi.model.BleSeat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.StateFlow
import kotlin.time.Duration

interface BleRepository {
    val bleState: StateFlow<BleState?>

    suspend fun shouldBeConnected(
        getBleSeat: suspend () -> BleSeat,
        onConnected: suspend () -> Unit = {},
        onDisconnected: suspend () -> Unit = {},
        connectionTimeout: Duration? = null,
        onTimeout: suspend () -> Unit = {},
    )

    suspend fun quit()

    suspend fun scanAndConnect(
        coroutineScope: CoroutineScope,
    )

    suspend fun BleState.scanAndConnect(coroutineScope: CoroutineScope): Peripheral

    suspend fun disconnect()


    fun getFilters(
        bleSeat: BleSeat,
    ): List<Filter> {
        val major = bleSeat.major.toInt()
        val minor = bleSeat.minor.toInt()

        return listOf(
            Filter.Service(BEACON_SERVICE_UUID),
            Filter.Name(bleSeat.name),
            Filter.Address(bleSeat.macAddress),
            Filter.ManufacturerData(
                id = MANUFACTURER_ID,
                data = byteArrayOf(
                    0, 0, 0, 0,
                    *uuidFrom(bleSeat.uuid).bytes,
                    major.shr(8).toByte(),
                    major.shr(0).toByte(),
                    minor.shr(8).toByte(),
                    minor.shr(0).toByte(),
                    0
                ),
                dataMask = MASK_UUID_MAJOR_MINOR,
            )
        )
    }


    companion object {
        val BEACON_SERVICE_UUID = uuidFrom("9FD42000-E46F-7C9A-57B1-2DA365E18FA1")
        val MANUFACTURER_ID = byteArrayOf(0, 0x59) // Nordic

        val MASK_UUID_MAJOR_MINOR = byteArrayOf(
            0, 0, 0, 0,
            -1, -1, -1, -1, -1, -1, -1, -1,
            -1, -1, -1, -1, -1, -1, -1, -1,
            -1, -1, -1, -1, 0,
        )
    }
}

data class BleState(
//    val shouldBeConnected: Boolean = false,
    // These are connected
    val bleSeat: BleSeat? = null,
    val scanner: Scanner? = null,

    val connectionTimeout: Duration? = null,
    val onTimeout: suspend () -> Unit = {},

    val foundPeripheral: Peripheral? = null,
    val foundPeripheralState: State? = null,
    val wasConnectedSuccessfully: Boolean = false,

    val onDisconnected: suspend () -> Unit = {},
    val onConnected: suspend () -> Unit = {},
)