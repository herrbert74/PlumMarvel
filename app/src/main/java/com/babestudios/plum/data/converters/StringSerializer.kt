package com.babestudios.plum.data.converters

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonParseException
import com.google.gson.JsonPrimitive
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonSerializer

import java.lang.reflect.Type

class StringSerializer : JsonSerializer<Boolean>, JsonDeserializer<String> {

	override fun serialize(arg0: Boolean?, arg1: Type, arg2: JsonSerializationContext): JsonElement {
		return JsonPrimitive(arg0!!)
	}

	@Throws(JsonParseException::class)
	override fun deserialize(arg0: JsonElement, arg1: Type, arg2: JsonDeserializationContext): String {
		return if (arg0.isJsonPrimitive) {
			if (arg0.asJsonPrimitive.isJsonNull) {
				"Unknown"
			} else {
				arg0.asString
			}
		} else "Unknown"
	}
}
