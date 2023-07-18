package io.github.jeddchoi.data.proto

import android.content.Context
import androidx.datastore.core.CorruptionException
import androidx.datastore.core.DataStore
import androidx.datastore.core.Serializer
import androidx.datastore.dataStore
import com.google.protobuf.InvalidProtocolBufferException
import io.github.jeddchoi.data.AppFlags
import java.io.InputStream
import java.io.OutputStream

object AppFlagsSerializer : Serializer<AppFlags> {
    override val defaultValue: AppFlags = AppFlags.getDefaultInstance()

    override suspend fun readFrom(input: InputStream): AppFlags {
        try {
            return AppFlags.parseFrom(input)
        } catch (exception: InvalidProtocolBufferException) {
            throw CorruptionException("Cannot read proto.", exception)
        }
    }

    override suspend fun writeTo(
        t: AppFlags,
        output: OutputStream
    ) = t.writeTo(output)
}

val Context.appFlagsDataStore: DataStore<AppFlags> by dataStore(
    fileName = "appFlags.pb",
    serializer = AppFlagsSerializer
)