package io.github.jeddchoi.thenewcafe.service

import com.juul.kable.Filter
import com.juul.kable.State
import kotlinx.coroutines.flow.StateFlow

interface BleRepository {
    val peripheralState: StateFlow<State?>
    fun scan(filters: List<Filter> = emptyList())
    fun getFilters(
        uuid: String? = null,
        service: String? = null,
        macAddress: String? = null,
        deviceName: String? = null,
        manufacturerData: ByteArray,
    )

    fun getManufacturerData(
        manufacturerId: ByteArray? = null,
        major: Int? = null,
        minor: Int? = null,
    )
}
