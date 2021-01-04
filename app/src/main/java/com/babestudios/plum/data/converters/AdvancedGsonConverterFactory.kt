package com.babestudios.plum.data.converters

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import okhttp3.ResponseBody
import retrofit2.Converter
import retrofit2.Retrofit
import java.lang.reflect.Type
import java.util.*

class AdvancedGsonConverterFactory private constructor(internal var gson: Gson) : Converter.Factory() {

	override fun responseBodyConverter(type: Type, annotations: Array<Annotation>,
									   retrofit: Retrofit): Converter<ResponseBody, *>? {
		val adapter = gson.getAdapter(TypeToken.get(type))
		return AdvancedGsonResponseBodyConverter(gson, adapter)
	}

	companion object {

		@JvmOverloads
		fun create(gson: Gson = GsonBuilder()//
				.registerTypeAdapter(Date::class.java, DateSerializer())//
				.registerTypeAdapter(Boolean::class.java, BooleanSerializer())//
				.registerTypeAdapter(Boolean::class.javaPrimitiveType, BooleanSerializer())//
				.registerTypeAdapter(String::class.java, StringSerializer())//
				.create()): AdvancedGsonConverterFactory {
			return AdvancedGsonConverterFactory(gson)
		}
	}
}
