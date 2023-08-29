package io.github.jeddchoi.firebase.functions

import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.serializer


@OptIn(InternalSerializationApi::class)
internal fun Any?.toJsonElement(json: Json = Json): JsonElement =
    when (this) {
        null -> JsonNull
        is Map<*, *> -> this.toJsonElement()
        is Collection<*> -> this.toJsonElement()
        is Boolean -> JsonPrimitive(this)
        is Number -> JsonPrimitive(this)
        is String -> JsonPrimitive(this)
        is Enum<*> -> JsonPrimitive(this.toString())
        else -> this.javaClass.kotlin.serializer().let { json.encodeToJsonElement(it, this) }
    }

private fun Collection<*>.toJsonElement(): JsonElement =
    JsonArray(this.map { it.toJsonElement() })

private fun Map<*, *>.toJsonElement(): JsonElement {
    return JsonObject(this.mapKeys { it.key.toString() }.mapValues { it.value.toJsonElement() })
}