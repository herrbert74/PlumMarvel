package com.babestudios.plum.data

import com.babestudios.base.rxjava.ErrorResolver
import com.babestudios.plum.Database
import io.reactivex.Single
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.HttpException
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class PlumErrorResolver @Inject constructor(private val database: Database) : ErrorResolver {

	/**
	 * Not applicable for this project
	 */
	override fun errorMessageFromResponseObject(errorObject: Any?): String? {
		return null
	}

	override fun errorMessageFromResponseBody(responseBody: ResponseBody): String {
		return try {
			val jsonObject = JSONObject(responseBody.string())
			jsonObject.getString("message")
		} catch (e: Exception) {
			"An error happened, try again."
		}
	}

	@Suppress("RemoveExplicitTypeArguments")
	override fun <T> resolveErrorForSingle(): (Single<T>) -> Single<T> {
		return { single ->
			single.onErrorResumeNext {
				(it as? HttpException)?.response()?.errorBody()?.let { body ->
					Single.error<T> { Exception(errorMessageFromResponseBody(body)) }
				} ?: Single.error<T> { Exception("An error happened") }
			}
		}
	}

}
