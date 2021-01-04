package com.babestudios.plum.data.converters

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonParseException
import com.google.gson.JsonPrimitive
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonSerializer

import java.lang.reflect.Type

class BooleanSerializer : JsonSerializer<Boolean>, JsonDeserializer<Boolean> {

	override fun serialize(arg0: Boolean?, arg1: Type, arg2: JsonSerializationContext): JsonElement {
		return JsonPrimitive(arg0!!)
	}

	@Throws(JsonParseException::class)
	override fun deserialize(arg0: JsonElement, arg1: Type, arg2: JsonDeserializationContext): Boolean? {
		return if (arg0.isJsonPrimitive) {
			when {
				arg0.asJsonPrimitive.isBoolean -> arg0.asBoolean
				arg0.asJsonPrimitive.isString -> arg0.asString == "1"
				else -> false
			}
		} else false
	}
}
