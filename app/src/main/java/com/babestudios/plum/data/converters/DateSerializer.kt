package com.babestudios.plum.data.converters

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonParseException

import java.lang.reflect.Type
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

internal class DateSerializer : JsonDeserializer<Date> {
	private val dateFormats = arrayOf("yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd")

	@Throws(JsonParseException::class)
	override fun deserialize(jsonElement: JsonElement, typeOF: Type, context: JsonDeserializationContext): Date? {
		for (format in dateFormats) {
			try {
				return SimpleDateFormat(format, Locale.US).parse(jsonElement.asString)
			} catch (ignored: ParseException) {
			}

		}
		throw JsonParseException("Unparseable date: \"" + jsonElement.asString + "\". Supported formats: "
				+ dateFormats.contentToString())
	}
}
